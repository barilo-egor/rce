Ext.define('Main.view.paymentTypes.PaymentTypeWindow', {
    extend: 'Ext.window.Window',
    bind: {
        title: '{title}'
    },
    width: '95%',
    height: '95%',
    modal: true,
    autoShow: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    requires: [
        'Main.view.paymentTypes.CreatePaymentTypeController',
        'Main.view.paymentTypes.requisites.CreateRequisiteWindow',
        'Main.view.paymentTypes.requisites.RequisiteForm',
        'Main.view.paymentTypes.PaymentTypeForm',
        'Main.view.paymentTypes.RequisiteGrid'
    ],
    controller: 'createPaymentTypeController',

    buttonAlign: 'center',
    buttons: [
        {
            xtype: 'savebutton',
            handler: 'save'
        },
        {
            xtype: 'cancelbutton',
            handler: ExtUtil.closeWindow
        }
    ],
    items: [
        {
            xtype: 'paymenttypeform'
        },
        {
            xtype: 'requisitegrid'
        }
    ]
})