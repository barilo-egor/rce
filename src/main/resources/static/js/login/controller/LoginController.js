Ext.define('Login.controller.LoginController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.loginController',

    login: function () {
        let form = ExtUtil.idQuery('loginForm')
        Ext.Ajax.request({
            method: 'POST',
            url: '/web/main',
            async: false,
            params: form.getValues(),
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                let errorLoginContainer = ExtUtil.idQuery('errorLoginContainer')
                if (response.error) errorLoginContainer.show()
                else if (response.loginSuccess) {
                    errorLoginContainer.hide()
                    document.location.href = response.loginUrl
                }
            }
        })
    },

    specialKeyPress: function (field, e) {
        if (e.getKey() === e.ENTER) {
            this.login()
        }
    }
})