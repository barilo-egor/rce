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

    createGridPanel(fiatCurrency) {
        Ext.define('bulkDiscountModel', {
            extend: 'Ext.data.Model',
            fields: ['value', 'percent'],
        });
        let store = Ext.create('Ext.data.Store', {
            model: 'bulkDiscountModel',
            pageSize : 100,
            proxy: {
                type: 'memory',
                enablePaging: true,
                reader: {
                    type: 'json'
                }
            }
        });
        store.getProxy().setData(fiatCurrency.bulkDiscounts);
        store.load();
        return {
            title: fiatCurrency.displayName,
            items: [
                {
                    xtype: 'bulkdiscountgridpanel',
                    store: store
                }
            ]
        }
    },

})