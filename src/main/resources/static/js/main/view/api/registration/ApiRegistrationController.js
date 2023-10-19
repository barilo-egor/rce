Ext.define('Main.view.api.registration.ApiRegistrationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.apiRegistrationController',

    register: function (btn) {
        let form = ExtUtil.idQuery('apiuserregisteform')
        let result
        Ext.Ajax.request({
            url: 'api/user/isExistById',
            params: {
                id: ExtUtil.idQuery('idField').getValue()
            },
            method: 'GET',
            async: false,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                if (response.result) result = 'Данный идентификатор уже занят'
                else result = true
            }
        })
        if (!result) {
            Ext.Msg.alert('Внимание', 'Такой логин уже занят.')
            return
        }
        if (!form.isValid()) {
            ExtMessages.incorrectlyForm()
            return
        }
        let jsonData = form.getValues()
        form.setLoading('Загрузка')
        Ext.Function.defer(function() {
            Ext.Ajax.request({
                url: 'api/user/save',
                method: 'POST',
                jsonData: jsonData,
                success: function (rs) {
                    let response = Ext.JSON.decode(rs.responseText).body.data
                    Ext.Msg.alert('Сохранено',
                        '<div style="text-align: center;">Токен пользователя: <p/><b>' + response.token + '</b></div>')
                    form.reset()
                    form.setLoading(false)
                }
            })
        }, 10);
    }
})