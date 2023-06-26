Ext.define('Main.view.components.MainFramePanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'mainframepanel',
    padding: '10 0 0 0',
    id: 'mainFramePanel',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController'
    ],
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    scrollable: true,
    items: [
        {
            xtype: 'usdcoursepanel'
        }
    ]
})