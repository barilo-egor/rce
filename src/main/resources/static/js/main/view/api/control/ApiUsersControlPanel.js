Ext.define('Main.view.api.control.ApiUsersControlPanel', {
    xtype: 'apiuserscontrolpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'apiUsersControlController',
    title: {
        xtype: 'mainframetitle',
        text: 'Управление апи-пользователями'
    },
    scrollable: true,
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'container',
            padding: '20 20 20 20',
            items: [
                {
                    xtype: 'grid',
                    title: 'Апи-пользователи',
                    store: 'apiusersstore',
                    columns: [
                        {
                            width: 35,
                            dataIndex: 'isBanned',
                            renderer: function (val) {
                                if (val) {
                                    return '<i class="fas fa-circle redColor"></i>'
                                } else {
                                    return '<i class="fas fa-circle greenColor"></i>'
                                }
                            }
                        },
                        {
                            text: 'ID',
                            dataIndex: 'id',
                        },
                        {
                            text: 'Скидка',
                            dataIndex: 'personalDiscount'
                        },
                        {
                            text: 'Курс USD',
                            dataIndex: 'usdCourse'
                        },
                        {
                            text: 'Реквизит покупки',
                            dataIndex: 'buyRequisite',
                            flex: 1
                        },
                    ]
                }
            ]
        }
    ]
})