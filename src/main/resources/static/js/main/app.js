Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.application({
    name: 'Main',
    extend: 'Ext.app.Application',
    appFolder: '/js/main',
    title: 'Главное меню',
    autoCreateViewport: true,
    stores: [
        'Main.store.enum.FiatCurrenciesStore',
        'Main.store.tree.RequisitesTreeStore'
    ]
});