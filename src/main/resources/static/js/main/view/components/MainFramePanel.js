Ext.define('Main.view.components.MainFramePanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'mainframepanel',
    padding: '0 0 0 1',
    id: 'mainFramePanel',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController',
        'Main.view.components.MainFrameTitle',
        'Main.view.components.MainFrameHeader',
        'Main.view.api.registration.ApiRegistrationPanel',
        'Main.view.api.registration.ApiRegistrationController'
    ],
    layout: {
        type: 'fit'
    },
    items: [
        {
            xtype: 'apiregistrationpanel'
        }
    ]
})