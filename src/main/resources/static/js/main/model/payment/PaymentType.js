Ext.define('Main.model.payment.PaymentType', {
    extend: 'Ext.data.Model',
    idProperty: 'pid',
    fields: [
        {
            name: 'pid',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'isOn',
            type: 'boolean'
        },
        {
            name: 'minSum',
            type: 'number'
        },
        {
            name: 'dealType',
            type: 'string'
        },
        {
            name: 'isDynamicOn',
            type: 'string'
        },
        {
            name: 'fiatCurrency',
            type: 'string'
        }
    ]
})