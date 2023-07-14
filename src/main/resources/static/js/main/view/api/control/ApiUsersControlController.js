Ext.define('Main.view.api.control.ApiUsersControlController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.apiUsersControlController',
    requires: [
        'Main.view.api.control.ApiUserEditWindow'
    ],

    save: function (btn) {
        let form = ExtUtil.idQuery('editApiUserForm')
        if (!form.isValid()) {
            Ext.Msg.alert('Внимание', 'Неверно заполнена форма.')
            return
        }
        let selection = ExtUtil.idQuery('requisitesTree').getSelection()
        if (!selection || selection.length === 0) {
            Ext.Msg.alert('Внимание', 'Выберите реквизит для покупки')
            return
        }
        let requisitePid = selection[0].getData().pid
        if (!requisitePid) {
            Ext.Msg.alert('Внимание', 'Выберите реквизит для покупки')
            return
        }
        let jsonData = form.getValues()
        jsonData.buyRequisite = {
            pid: requisitePid
        }
        jsonData.isBanned = ExtUtil.idQuery('isBannedCheckBox').value
        form.setLoading('Загрузка')
        Ext.Function.defer(function() {
            Ext.Ajax.request({
                url: 'api/user/update',
                method: 'POST',
                jsonData: jsonData,
                success: function (rs) {
                    let response = Ext.JSON.decode(rs.responseText)
                    Ext.toast('Пользователь <b>' + response.id + '</b> обновлен.');
                    Ext.getStore('apiusersstore').load()
                    form.setLoading(false)
                    btn.up('window').close()
                }
            })
        }, 10);
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
    },

    hasAccessClick: function (me) {

    }
})