Ext.define('Main.view.components.MainFramePanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'mainframepanel',
    padding: '0 0 0 1',
    id: 'mainFramePanel',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController',
        'Main.view.components.MainFrameTitle',
        'Main.view.components.MainFrameHeader'
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