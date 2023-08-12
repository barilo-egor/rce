Ext.define('Registration.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
        'Registration.view.RegistrationPanel'
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