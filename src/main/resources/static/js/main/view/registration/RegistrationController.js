Ext.define('Main.view.registration.RegistrationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.registrationController',

    registerUser: function () {
        let form = ExtUtil.idQuery('registrationForm')
        if (!form.isValid()) {
            Ext.Msg.alert('Внимание', 'Неверно заполнена форма.');
            return
        }
        let registrationVO = {
            username: form.getValues().username,
            password: form.getValues().password
        }
        Ext.Ajax.request({
            url: '/web/registration/registerUser',
            method: 'POST',
            jsonData: registrationVO,
            success: function (rs) {
                Ext.Msg.alert('Информация', 'Пользователь зарегестрирован.')
                let formInputs = form.items.items
                for (let input of formInputs) {
                    input.setValue('')
                }
            },
            failure: function (rs) {
                Ext.Msg.alert('Ошибка', 'Ошибка при регистрации пользователя.')
            }
        })
    }
})