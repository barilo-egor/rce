Ext.define('Main.store.api.ApiUsersStore', {
    extend: 'Ext.data.Store',
    storeId: 'apiusersstore',

    model: 'Main.model.api.ApiUser',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/web/api/user/findAll',
        reader: {
            type: 'json',
            rootProperty: 'body.data'
        }
    }
});