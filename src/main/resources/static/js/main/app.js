Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.define('DisableColumnHideOverride', {
    override: 'Ext.grid.Panel',
    enableColumnHide: false
});
Ext.define('FixedWindows', {
    override: 'Ext.window.Window',
    draggable: false,
    resizable: false
})
Ext.application({
    name: 'Main',
    extend: 'Ext.app.Application',
    appFolder: '/js/main',
    title: 'Главное меню',
    autoCreateViewport: true,
    stores: [
        'Main.store.enum.FiatCurrenciesStore',
        'Main.store.enum.DealTypesStore',
        'Main.store.tree.RequisitesTreeStore',
        'Main.store.api.ApiUsersStore'
    ]
});