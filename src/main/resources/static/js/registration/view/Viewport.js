Ext.define('Registration.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Registration.view.RegistrationPanel',
        'Registration.view.RegistrationController'
    ],
    alias: 'widget.registrationViewport',
    layout: 'fit',
    viewModel: true,
    items: [
        {
            xtype: 'registrationpanel'
        }
    ]
});