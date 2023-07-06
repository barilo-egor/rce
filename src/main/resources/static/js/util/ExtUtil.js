let ExtUtil = {
    idQuery: function (id) {
        return Ext.ComponentQuery.query('[id=' + id + ']')[0]
    }
}