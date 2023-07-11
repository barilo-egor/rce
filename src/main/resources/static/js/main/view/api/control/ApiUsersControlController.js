Ext.define('Main.view.api.control.ApiUsersControlController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.apiUsersControlController',
    requires: [
        'Main.view.api.control.ApiUserEditWindow'
    ],

    save: function (btn) {
        alert('save')
    },

    delete: function (btn) {
        alert('delete')
    },

    editUserClick: function (grid, rowIndex, colIndex) {
        let apiUser = Ext.getStore('apiusersstore').getRange()[rowIndex].getData()
        Ext.create('Main.view.api.control.ApiUserEditWindow',
            {
                viewModel: {
                    data: {
                        apiUser: apiUser
                    }
                }
            }
            ).show()
    }
})