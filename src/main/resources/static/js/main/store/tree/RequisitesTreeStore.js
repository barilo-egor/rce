Ext.define('Main.store.tree.RequisitesTreeStore', {
    extend: 'Ext.data.TreeStore',
    storeId: 'requisitestreestore',
    proxy: {
        type: 'ajax',
        url: '/web/api/payment/requisite/tree?dealType=BUY',
        reader: {
            type: 'json',
            rootProperty: 'children'
        }
    },
    root: {
        text: 'Root',
        id: 'data',
        expanded: true
    }
})