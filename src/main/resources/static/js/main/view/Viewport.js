Ext.define('Main.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Main.view.MainPanel',
        'Main.view.MainController',
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController'
    ],
    alias: 'widget.mainViewport',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'mainpanel'
        }
    ]
});