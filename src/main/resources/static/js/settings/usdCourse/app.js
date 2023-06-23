Ext.Loader.setConfig({
    disableCaching: false,
});
Ext.application({
    name: 'UsdCourse',
    extend: 'Ext.app.Application',
    appFolder: '/js/settings/usdCourse',
    title: 'Замена курса USD',
    autoCreateViewport: true
});