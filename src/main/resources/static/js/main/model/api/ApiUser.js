Ext.define('Main.model.api.ApiUser', {
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
            type: 'number'
        },
        {
            name: 'registrationDate',
            type: 'date'
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
            name: 'requisites',
            type: 'string'
        },
        {
            name: 'usdCourse',
            type: 'number'
        }
    ]
});