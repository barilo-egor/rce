Ext.define('Main.model.api.ApiUser', {
    extend: 'Ext.data.Model',
    idProperty: 'pid',
    fields: [
        {
            name: 'pid',
            type: 'int'
        },
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'personalDiscount',
            type: 'number',
        },
        {
            name: 'registrationDate',
            convert: function (value) {
                return new Date(value);
            }
        },
        {
            name: 'isBanned',
            type: 'boolean'
        },
        {
            name: 'token',
            type: 'string'
        },
        {
            name: 'buyRequisite',
            type: 'string'
        },
        {
            name: 'usdCourse',
            type: 'number'
        }
    ]
});