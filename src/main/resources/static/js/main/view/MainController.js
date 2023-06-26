Ext.define('Main.view.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.mainController',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController',
        'Main.view.registration.RegistrationPanel',
        'Main.view.registration.RegistrationController'
    ],

    collapse: function (btn) {
        let toolBar = Ext.ComponentQuery.query('[id=mainToolBar]')[0]
        if (toolBar.hidden) {
            toolBar.show()
        } else {
            toolBar.hide()
        }
    },

    usdCourseClick: function (btn) {
        this.mainToolBarClick(btn, 'usdcoursepanel')
    },

    bulkDiscountClick: function (btn) {
        this.mainToolBarClick(btn, 'bulkdiscountpanel')
    },

    newWebUserClick: function (btn) {
        this.mainToolBarClick(btn, 'registrationpanel')
    },

    mainToolBarClick: function (btn, panel) {
        Ext.ComponentQuery.query('[id=mainPanel]')[0].setLoading('Загрузка')
        Ext.Function.defer(function() {
            let toolbar = btn.up('toolbar')
            toolbar.hide()
            let mainFramePanel = Ext.ComponentQuery.query('[id=mainFramePanel]')[0]
            mainFramePanel.items.items.forEach(item => item.destroy())
            mainFramePanel.insert({xtype: panel})
            mainFramePanel.update();
            mainFramePanel.updateLayout();
            Ext.ComponentQuery.query('[id=mainPanel]')[0].setLoading(false)
        }, 10);
    },
})