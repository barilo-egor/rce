Ext.define('Main.store.enum.DealTypesStore', {
    extend: 'Ext.data.Store',
    alias: 'store.dealTypesStore',
    storeId: 'dealTypesStore',
    fields: ['name', 'nominative'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/web/enum/dealTypes',
        reader: {
            type: 'json',
            rootProperty: 'body.data'
        }
    }
});