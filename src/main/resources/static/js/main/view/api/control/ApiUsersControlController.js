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
        let me = btn
        Ext.Msg.show({
            title:'Удаление пользователя',
            message: 'Вы уверены, что хотите удалить пользователя?',
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(btn) {
                if (btn === 'yes') {
                    Ext.Ajax.request({
                        url: 'api/user/delete',
                        method: 'DELETE',
                        params: {
                            pid: me.up('window').getViewModel().data.apiUser.pid
                        },
                        success: function (rs) {
                            if (rs.responseText === 'true') {
                                me.up('window').close()
                                Ext.toast('Пользователь удален.')
                                Ext.getStore('apiusersstore').load()
                            } else Ext.Msg.alert('Ошибка', 'Возникли ошибки при удалении.')
                        }
                        ,
                        failure: function () {
                            Ext.Msg.alert('Ошибка', 'Возникли ошибки при удалении.')
                        }
                    })
                }
            }
        });
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