Ext.define('Registration.view.RegistrationController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.registrationController',

    validateInput: function (val) {
        if (/^[A-Za-z0-9]*$/.test(val)) return true;
        else return 'Ошибка'
    },

    validateConfirmPassword: function (val) {
        let passwordInput = Ext.ComponentQuery.query('[id=passwordInput]')
        if (passwordInput !== val) return 'Пароли не совпадают'
        return true
    },

    registerUser: function () {
        let form = Ext.ComponentQuery.query('[id=registrationForm]')[0]
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
                Ext.alert('Ошибка', 'Ошибка при регистрации пользователя.')
            }
        })
    }
})