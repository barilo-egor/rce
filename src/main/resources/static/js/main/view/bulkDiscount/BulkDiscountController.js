Ext.define('Main.view.bulkDiscount.BulkDiscountController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bulkDiscountController',
    requires: [
        'Main.view.bulkDiscount.BulkDiscountGridPanel'
    ],
    bulkDiscountsAfterRender: function () {
        let me = this

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
                        store: store
                    }
                ]
            };
            fiatCurrencyTabPanel.items[0].items.push(dealTypeTabPanel);
        }
        return fiatCurrencyTabPanel;
    },

    onAddClick: function () {
        let view = this.getView(),
            rec = new Main.view.bulkDiscount.model.BulkDiscountModel({
                sum: '',
                percent: '',
                fiatCurrency: view.up().up().up().title,
                dealType: view.up().title,
            });

        view.store.insert(0, rec);
        view.findPlugin('cellediting').startEdit(rec, 0);
    },

    onRemoveClick: function(view, recIndex, cellIndex, item, e, record) {
        record.drop();
    },

    onSaveClick: function (btn) {
        let bulkDiscounts = [];
        // for (let grid of Ext.ComponentQuery.query('grid')) {
        //     let store = grid.getStore();
        //     for (let bulkDiscount of store.getModifiedRecords()) {
        //         bulkDiscounts.push(store.getById(bulkDiscount.data.id).data)
        //     }
        // }

        for (let grid of Ext.ComponentQuery.query('grid')) {
            for (let bulkDiscount of grid.getStore().data.items) {
                    bulkDiscounts.push(bulkDiscount.data)
            }
        }

        Ext.Ajax.request({
            url: '/web/bulk_discount/saveDiscounts',
            method: 'POST',
            jsonData: bulkDiscounts,
            success: function (rs) {
            }
        })
    }

})