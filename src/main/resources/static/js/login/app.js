Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.application({
    name: 'Login',
    extend: 'Ext.app.Application',
    appFolder: '/js/login',
    title: 'Вход',
    autoCreateViewport: true
});