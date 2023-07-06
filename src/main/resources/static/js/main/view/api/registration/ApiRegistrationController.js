Ext.define('Main.view.api.registration.ApiRegistrationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.apiRegistrationController',

    register: function (btn) {
        let form = Ext.ComponentQuery.query('[id=apiuserregisteform]')[0]
        if (!form.isValid()) {
            Ext.Msg.alert('Внимание', 'Неверно заполнена форма.')
            return
        }
        form.setLoading('Загрузка')
        Ext.Function.defer(function() {
            Ext.Ajax.request({
                url: 'api/user/create',
                method: 'POST',
                jsonData: form.getValues(),
                success: function (rs) {
                    let response = Ext.JSON.decode(rs.responseText)
                    Ext.Msg.alert('Сохранено',
                        '<div style="text-align: center;">Токен пользователя: <p/><b>' + response.token + '</b></div>')
                    form.reset()
                    form.setLoading(false)
                }
            })
        }, 10);
    }
})