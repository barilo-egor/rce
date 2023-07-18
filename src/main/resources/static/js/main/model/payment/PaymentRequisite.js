Ext.define('Main.model.payment.PaymentRequisite', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'pid',
            type: 'int'
        },
        {
            name: 'paymentType',
            type: 'auto'
        },
        {
            name: 'requisite',
            type: 'string'
        },
        {
            name: 'requisiteOrder',
            type: 'int'
        }
    ]
})