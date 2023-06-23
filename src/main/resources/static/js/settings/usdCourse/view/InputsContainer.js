Ext.define('UsdCourse.view.InputsContainer', {
    extend: 'Ext.container.Container',
    xtype: 'inputsContainer',
    controller: 'usdCourseController',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    defaults: {
        labelWidth: 70
    },
    padding: '0 0 5 0'
})