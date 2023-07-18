Ext.define('Main.model.enum.FiatCurrency', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'code',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'genitive',
            type: 'string'
        },
        {
            name: 'flag',
            type: 'string'
        }
    ]
});