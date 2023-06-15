Ext.define('UsdCourse.view.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Ext.form.Panel',
    alias: 'widget.UsdCourse-panel',
    title: 'Замена курса USD',
    header: {
        titleAlign: 'center'
    },
    bodyStyle: 'padding:5px 5px',
    region: 'center',
    width: 1000,
    heigth: 1000,
    layout: {
        type: 'vbox',
        align: 'center'
    },
    items: [
        {
            xtype: 'form',
            id: 'coursesForm',
            width: 300,
            height: 200,
            listeners: {
                beforerender: function (me) {
                    console.log('qwe')
                    Ext.Ajax.request({
                        url: '/settings/getUsdCourses',
                        method: 'GET',
                        success: function (rs) {
                            let response = Ext.JSON.decode(rs.responseText);
                            let items = [];
                            let data = response.data;
                        }
                    })
                }
            },
            buttons: [
                {
                    text: 'Сохранить',
                    handler: function () {
                        // действие отправки
                    }
                }
            ]
        }
    ]
});
