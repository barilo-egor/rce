Ext.define('Main.view.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.mainController',

    setIsNotAdmin: function (me) {
        ExtUtil.request({
            url: '/web/roles/getRole',
            method: 'GET',
            async: false,
            success: function (response) {
                me.setViewModel({
                    data: {
                        isNotAdmin: response.body.data.filter(role => role.name === 'ROLE_ADMIN').length === 0
                    }
                })
            }
        })
    },

    collapse: function (btn) {
        let toolBar = ExtUtil.idQuery('mainToolBar')
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

    newApiUserClick: function (btn) {
        this.mainToolBarClick(btn, 'apiregistrationpanel')
    },

    apiUsersControlClick: function (btn) {
        this.mainToolBarClick(btn, 'apiuserscontrolpanel')
    },

    mainToolBarClick: function (btn, panel) {
        ExtUtil.idQuery('mainPanel').setLoading('Загрузка')
        Ext.Function.defer(function() {
            let toolbar = btn.up('toolbar')
            toolbar.hide()
            let mainFramePanel = ExtUtil.idQuery('mainFramePanel')
            mainFramePanel.items.items.forEach(item => item.destroy())
            mainFramePanel.insert({xtype: panel})
            mainFramePanel.update();
            mainFramePanel.updateLayout();
            ExtUtil.idQuery('mainPanel').setLoading(false)
        }, 10);
    },

    changePasswordClick: function (btn) {
        this.mainToolBarClick(btn, 'changepasswordpanel')
    },

    paymentTypesClick: function (btn) {
        this.mainToolBarClick(btn, 'paymenttypespanel')
    }
})