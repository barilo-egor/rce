Ext.define('Main.store.api.ApiUsersStore', {
    extend: 'Ext.data.Store',
    alias: 'store.apiUsers',
    model: 'KitchenSink.model.Company',

    autoLoad: true,
    pageSize: null,

    // proxy: {
    //     type: 'ajax',
    //     url: '/KitchenSink/Company',
    //
    //     reader: {
    //         type: 'json',
    //         rootProperty: 'data',
    //
    //         // Do not attempt to load orders inline.
    //         // They are loaded through the proxy
    //         implicitIncludes: false
    //     }
    // }
});