Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.application({
    name: 'MainUser',
    extend: 'Ext.app.Application',
    appFolder: '/js/mainUser',
    title: 'Главная',
    autoCreateViewport: true
});