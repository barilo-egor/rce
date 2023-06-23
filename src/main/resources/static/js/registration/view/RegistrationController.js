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

    ok: function () {
        alert('ok')
    }
})