Ext.define('Main.view.bulkDiscount.BulkDiscountController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bulkDiscountController',
    requires: [
        'Main.view.bulkDiscount.BulkDiscountGridPanel',
        'Main.view.bulkDiscount.BulkDiscountAddForm'
    ],
    bulkDiscountsAfterRender: function () {
        let me = this;
        let discountsTabPanel = Ext.ComponentQuery.query('[id=discountsTabPanel]')[0];
        Ext.Ajax.request({
            url: '/web/bulk_discount/getDiscounts',
            method: 'GET',
            async: false,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText);
                let data = response.data;
                for (let fiatCurrency of data) {
                    discountsTabPanel.insert(me.createGridPanel(fiatCurrency));
                }
                discountsTabPanel.setActiveTab(0);
            }
        })
    },

    createGridPanel: function (fiatCurrency) {
        let fiatCurrencyTabPanel = {
            title: fiatCurrency.name,
            layout: {
                type: 'fit'
            },
            items: [
                {
                    xtype: 'tabpanel',
                    items: []
                }
            ]
        };
        for (let dealType of fiatCurrency.dealTypes) {
            let store = Ext.create('Main.view.bulkDiscount.store.BulkDiscountStore');
            for (let bulkDiscount of dealType.bulkDiscounts) {
                bulkDiscount.fiatCurrency = fiatCurrency.name;
                bulkDiscount.dealType = dealType.name;
            }
            store.getProxy().setData(dealType.bulkDiscounts);
            store.load();
            let dealTypeTabPanel = {
                title: dealType.displayName,
                layout: {
                    type: 'fit'
                },
                items: [
                    {
                        xtype: 'bulkdiscountgridpanel',
                        store: store,
                        fiatCurrency: fiatCurrency.name,
                        dealType: dealType.name
                    }
                ]
            };
            fiatCurrencyTabPanel.items[0].items.push(dealTypeTabPanel);
        }
        return fiatCurrencyTabPanel;
    },

    onSaveClick: function (btn) {
        let me = this;
        let oldSum = ExtUtil.idQuery('oldSum').getValue();
        let bulkDiscount = {
            sum: ExtUtil.idQuery('sum').getValue(),
            percent: ExtUtil.idQuery('percent').getValue(),
            fiatCurrency: ExtUtil.idQuery('fiatCurrency').getValue(),
            dealType: ExtUtil.idQuery('dealType').getValue()
        };
        me.saveDiscountRequest(bulkDiscount, oldSum);
        btn.up('window').close();
    },

    saveDiscountRequest: function (bulkDiscount, oldSum) {
        let me = this;
        let requestBody = {
            url: '/web/bulk_discount/saveDiscount',
            method: 'POST',
            jsonData: bulkDiscount,
            success: function () {
                for (let grid of Ext.ComponentQuery.query('grid')) {
                    if (grid.fiatCurrency === bulkDiscount.fiatCurrency && grid.dealType === bulkDiscount.dealType) {
                        let store = grid.getStore();
                        let recs = store.getData().getRange();
                        if (oldSum) {
                            for (let rec of recs) {
                                if (rec.getData().sum === oldSum) {
                                    rec.data.percent = bulkDiscount.percent;
                                    if (bulkDiscount.sum !== oldSum) {
                                        rec.data.sum = bulkDiscount.sum;
                                    }
                                    store.add(rec);
                                    break;
                                }
                            }
                        } else {
                            let newRec = new Main.view.bulkDiscount.model.BulkDiscountModel({
                                sum: bulkDiscount.sum,
                                percent: bulkDiscount.percent,
                                fiatCurrency: bulkDiscount.fiatCurrency,
                                dealType: bulkDiscount.dealType,
                            });
                            store.add(newRec);
                        }
                        let sortersItems = store.getSorters().items;
                        if (sortersItems.length !== 0) {
                            store.sort('sum', sortersItems[0]._direction);
                        } else {
                            store.sort('sum', 'DESC');
                        }
                        break;
                    }
                }
                Ext.Msg.alert('Информация', 'Скидка сохранена.');
            },
            failure: function () {
                Ext.Msg.alert('Ошибка', 'При сохранении произошли ошибки.')
            }
        };
        if (oldSum) requestBody.params = {
            oldSum: oldSum
        }
        Ext.Ajax.request(requestBody);
    },

    onAddClick: function (btn) {
        let grid = btn.up('grid');
        Ext.widget('bulkdiscountaddform', {
            viewModel: {
                data: {
                    fiatCurrency: grid.fiatCurrency,
                    dealType: grid.dealType
                }
            },
        }).show()
    },

    onEditClick: function (view, recIndex, cellIndex, item, e, record) {
        let grid = view.up('grid');
        Ext.widget('bulkdiscountaddform', {
            viewModel: {
                data: {
                    oldSum: record.data.sum,
                    sum: record.data.sum,
                    percent: record.data.percent,
                    fiatCurrency: grid.fiatCurrency,
                    dealType: grid.dealType
                }
            },
        }).show()
    },

    onRemoveClick: function(view, recIndex, cellIndex, item, e, record) {
        let bulkDiscount = {
            sum: record.data.sum,
            fiatCurrency: record.data.fiatCurrency,
            dealType: record.data.dealType
        };
        Ext.Ajax.request({
            url: '/web/bulk_discount/removeDiscount',
            method: 'DELETE',
            jsonData: bulkDiscount,
            success: function () {
                record.drop();
                Ext.Msg.alert('Информация', 'Скидка удалена.');
            },
            failure: function () {
                Ext.Msg.alert('Ошибка', 'При удалении произошли ошибки.')
            }
        });
    },

})