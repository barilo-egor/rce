Ext.define('UsdCourse.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'UsdCourse.view.UsdCoursePanel',
        'UsdCourse.view.UsdCourseController'
    ],
    alias: 'widget.UsdCoursePanel-vp',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'usdcoursepanel'
        }
    ]
});