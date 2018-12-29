import * as React from 'react';
import '../../App.css';
import { Button, Table, Drawer, message } from "antd";
import 'antd/dist/antd.css';
import { setNavType } from '../../rpc-redux/actions'
import { withRouter } from 'react-router-dom';
import { connect, ConnectedComponentClass } from 'react-redux';
import { serviceDetail } from '../../rpc-redux/actions'
import Fetch from '../../Fetch';
interface ITableType {
    service: string,
    group: string,
    appName: string
}
interface IMethodTableType {
    methodName: string,
    paramType: string[],
    returnType: string
}
class IndexSearch extends React.Component<any, any> {
    public state = {
        visible: true,
        methods: [
            {
                "methodName": "sayHello1",
                "paramType": [
                    "java.util.List"
                ],
                "returnType": "void"
            },
            {
                "methodName": "sayHello",
                "paramType": [
                    "java.lang.String"
                ],
                "returnType": "void"
            },
            {
                "methodName": "getHello",
                "paramType": [

                ],
                "returnType": "java.lang.String"
            },
            {
                "methodName": "updateUser",
                "paramType": [
                    "com.nix.jingxun.addp.rpc.producer.test.User",
                    "java.lang.Boolean",
                    "com.nix.jingxun.addp.rpc.producer.test.User",
                    "com.nix.jingxun.addp.rpc.producer.test.User",
                    "com.nix.jingxun.addp.rpc.producer.test.User"
                ],
                "returnType": "com.nix.jingxun.addp.rpc.producer.test.User"
            }
        ]
    }
    constructor(props: any) {
        super(props);
        window['AddpContext']['IndexSearch'] = {};
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['IndexSearch'] = this.state);
    }
    public showDrawer = () => {
        this.setState({
            visible: true,
        });
    }
    public onClose = () => {
        this.setState({
            visible: false,
        });
    }
    public onClickDetail = (key: string) => () => {
        this.props.history.push({ pathname: "/detail" });
        this.props.dispatch(serviceDetail(key));
    }
    public onClickServiceTest = (sign: string) => () => {
        Fetch(`/ops/detail/?sign=${sign}`, {
            method: 'GET'
        }).then((detail: any) => {
            console.log('data = ', detail);
            this.showDrawer();
        }).catch(error => {
            console.log("error", error);
            message.error('获取服务详情失败！！！');
        });
    }
    public render() {
        this.props.dispatch(setNavType('index_search'));
        console.log("IndexSearch start...");
        return (
            <div className='conetntDiv'>
                <h4>查找到的RPC服务10条数据</h4>
                <hr />
                <Table<ITableType> dataSource={this.props.redux.data.serviceList} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                    <Table.Column<ITableType> key="service" title="服务ID" dataIndex="service" width="30%" />
                    <Table.Column<ITableType> key="group" title="分组" dataIndex="group" width="15%" />
                    <Table.Column<ITableType> key="appName" title="应用名" dataIndex="appName" width="15%" />
                    <Table.Column key="操作" title="操作" render={this.createOperationColumn} width="20%" />
                </Table>
                <Drawer
                    title="Basic Drawer"
                    placement="top"
                    height={700}
                    closable={false}
                    onClose={this.onClose}
                    visible={this.state.visible}
                >
                    <Table<IMethodTableType> dataSource={this.state.methods}  >
                        <Table.Column<IMethodTableType> key="methodName" title="方法名" dataIndex="methodName" width="20%" />
                        <Table.Column<IMethodTableType> key="paramTypes" title="参数类型" dataIndex="paramType" width="50%"
                            render={(paramType: any[]): any => {
                                return (<div style={{maxWidth:'800px'}}>
                                    {paramType.map(item => (
                                        <span className='paramType'>{item}</span>)
                                    )}
                                </div>)
                            }}
                        />
                        <Table.Column<IMethodTableType> key="returnType" title="返回类型" dataIndex="returnType" width="15%" />
                        <Table.Column key="操作" title="操作" width="15%" render={(rowData) =>
                            <Button type="primary" onClick={this.onClickServiceTest(rowData.key)}>测试</Button>
                        }
                        />
                    </Table>
                </Drawer>
            </div >
        );
    };
    private createOperationColumn = (rowData: any) => {
        return <div>
            <Button type="primary" onClick={this.onClickDetail(rowData.key)}>详情</Button>
            <Button type="primary" onClick={this.onClickServiceTest(rowData.key)}>测试</Button>
        </div>;
    }
}
export default withRouter((connect(
    (state: any) => {
        return {
            redux: state.index
        }
    },
    (dispatch) => ({
        dispatch: (func: any) => func(dispatch)
    }),
    null,
    { withRef: false }
)(IndexSearch) as ConnectedComponentClass<typeof IndexSearch, any>));