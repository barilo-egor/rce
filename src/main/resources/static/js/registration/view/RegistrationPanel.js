Ext.define('Registration.view.RegistrationPanel', {
    xtype: 'registrationpanel',
    extend: 'Ext.form.Panel',
    controller: 'registrationController',
    title: 'Регистрация нового пользователя',
    header: {
        titleAlign: 'center'
    },
    region: 'center',
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
})