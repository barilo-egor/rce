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
            title: fiatCurrency.displayName,
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
                bulkDiscount.fiatCurrency = fiatCurrency.displayName;
                bulkDiscount.dealType = dealType.displayName;
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
                        fiatCurrency: fiatCurrency.displayName,
                        dealType: dealType.displayName
                    }
                ]
            };
            fiatCurrencyTabPanel.items[0].items.push(dealTypeTabPanel);
        }
        return fiatCurrencyTabPanel;
    },

    onSaveClick: function (btn) {
        let me = this;
        let form = btn.up('form');
        let oldSum = form.getValues().oldSum;
        let values = form.getValues();
        let bulkDiscount = {
            sum: values.sum,
            percent: values.percent,
            fiatCurrency: values.fiatCurrency,
            dealType: values.dealType
        };
        me.saveDiscountRequest(bulkDiscount, oldSum);
        form.up('window').close();
    },

    saveDiscountRequest: function (bulkDiscount, oldSum) {
        let requestBody = {
            url: '/web/bulk_discount/saveDiscount',
            method: 'POST',
            jsonData: bulkDiscount,
            success: function (rs) {
                Ext.Msg.alert('Информация', 'Скидка сохранена.');
                // let newRec = new Main.view.bulkDiscount.model.BulkDiscountModel({
                //     sum: sum,
                //     percent: percent,
                //     fiatCurrency: view.up().up().up().title,
                //     dealType: view.up().title,
                // });
                // let store = view.getStore();
                // let recs = store.getData().getRange();
                // if (recs.length === 0) store.add(newRec)
                // else {
                //     if (recs[recs.length - 1].getData().sum > sum) store.insert(recs.length, newRec);
                //     else {
                //         for (let rec of recs) {
                //             if (sum > rec.getData().sum) {
                //                 store.insert(store.indexOf(rec), newRec);
                //                 break;
                //             }
                //         }
                //     }
                // }
            },
            failure: function (rs) {
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
            success: function (rs) {
                Ext.Msg.alert('Информация', 'Скидка удалена.');
                record.drop();
            },
            failure: function (rs) {
                Ext.Msg.alert('Ошибка', 'При удалении произошли ошибки.')
            }
        });
    },

    // onSaveClick: function () {
    //     let addedBulkDiscounts = [];
    //     let updatedBulkDiscounts = [];
    //     let removedBulkDiscounts = [];
    //     let bulkDiscounts = [];
    //     for (let grid of Ext.ComponentQuery.query('grid')) {
    //         let store = grid.getStore();
    //         for (let bulkDiscount of store.getModifiedRecords()) {
    //             if (bulkDiscount.crudState === "C") addedBulkDiscounts.push(bulkDiscount.data)
    //             else updatedBulkDiscounts.push(bulkDiscount.data)
    //         }
    //         for (let bulkDiscount of store.getRemovedRecords()) {
    //             removedBulkDiscounts.push(bulkDiscount.data)
    //         }
    //     }
    //     bulkDiscounts.push(addedBulkDiscounts);
    //     bulkDiscounts.push(updatedBulkDiscounts);
    //     bulkDiscounts.push(removedBulkDiscounts);
    //     // for (let grid of Ext.ComponentQuery.query('grid')) {
    //     //     for (let bulkDiscount of grid.getStore().data.items) {
    //     //             bulkDiscounts.push(bulkDiscount.data)
    //     //     }
    //     // }
    //     Ext.Ajax.request({
    //         url: '/web/bulk_discount/saveDiscounts',
    //         method: 'POST',
    //         jsonData: bulkDiscounts,
    //         success: function (rs) {
    //             Ext.Msg.alert('Информация', 'Скидки были успешно обновлены.');
    //         }
    //     })
    // }

})