Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.application({
    name: 'ApiDocumentation',
    extend: 'Ext.app.Application',
    appFolder: '/js/api/documentation',
    title: 'API документация',
    autoCreateViewport: true
});