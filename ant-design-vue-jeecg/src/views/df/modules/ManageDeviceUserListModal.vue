<template>
  <div>
    <a-modal
      :title="title"
      :width="800"
      :visible="manage"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭">
      <form :autoFormCreate="(form) => this.form = form" >
        <a-table
          :columns="columns"
          :dataSource="data"
          :pagination="false"
        >
          <template v-for="(col, i) in [ 'userRate','lowerLimit','upperLimit']" :slot="col" slot-scope="text, record, index">
            <a-input
              :key="col"
              v-if="record.editable"
              style="margin: -5px 0"
              :value="text"
              :placeholder="columns[i+4].title"
              @change="e => handleChange(e.target.value, record.key, col)"
            />
            <template v-else>{{ text }}</template>
          </template>
          <template slot="operation" slot-scope="text, record, index">
            <template >
              <span >
                <a-popconfirm title="是否要删除此行？" @confirm="remove(record)">
                  <a>删除</a>
                </a-popconfirm>
              </span>
            </template>
          </template>
        </a-table>
      </form>
    </a-modal>
  </div>

</template>

<script>
import {getAction,httpAction} from '@/api/manage'
import JCheckbox from '@/components/jeecg/JCheckbox'
import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
import STable from '@/components/table/'
export default {
  name: "ManageProductChannelsModal",
  components: {
    JCheckbox,
    JMultiSelectTag,
    STable
  },
  data () {
    return {
      title:"商户关联设备信息",
      description: '高级表单常见于一次性输入和提交大批量数据的场景。',
      loading: false,
      manage:false,
      userName:"",
      deviceCode:"",
      // table
      columns: [
        {
          title: '商户名称',
          dataIndex: 'userName',
          key: 'userName',
          width: '20%',
          scopedSlots: { customRender: 'userName' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width: '10%',
          customRender: function(text) {
            if (text == '2') {
              return <a-tag color="red">禁用</a-tag>
            } else if (text == '1') {
              return  <a-tag color="cyan">已启动</a-tag>
            } else{
              return text
            }
          }
        },
        {
          title: '设备名称',
          dataIndex: 'deviceName',
          key: 'deviceName',
          width: '20%',
          scopedSlots: { customRender: 'deviceName' }
        },
        {
          title: '设备编码',
          dataIndex: 'deviceCode',
          key: 'deviceCode',
          width: '20%',
          scopedSlots: { customRender: 'deviceCode' }
        },
        {
          title: '操作',
          key: 'action',
          scopedSlots: { customRender: 'operation' }
        }
      ],
      data: [],
      url:{
        findDeviceUserInfo:"/df/deviceUserEntity/findDeviceUserInfo",
        deleteDeviceUser:"/df/deviceUserEntity/deleteDeviceUser",
      }
    }
  },
  mounted:function () {
  },
  methods: {
    listManage(record){
      this.manage=true;
      this.deviceCode=record.deviceCode;
      this.findDeviceUserInfo();
    },
    findDeviceUserInfo(){
      let formData = [];
      formData.deviceCode = this.deviceCode;
      getAction(this.url.findDeviceUserInfo,formData).then((res)=>{
        if(res.success){
          this.data=res.result;
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      })
    },
    close() {
      this.$emit('close');
      this.manage = false;
    },
    handleOk(){
      this.$emit('close');
      this.manage = false;
    },
    handleCancel() {
      this.close()
    },
    handleSubmit (e) {
      e.preventDefault()
    },
    newMember () {
      this.data.push({
        key: '-1',
        name: '',
        workId: '',
        department: '',
        editable: true,
        isNew: true
      })
    },
    remove (record) {
      let formData = {deviceCode:record.deviceCode,userName:record.userName};
      httpAction(this.url.deleteDeviceUser,formData,"post").then((res)=>{
        if(res.success){
          this.$message.success(res.message);
          this.manage=false;
          this.$emit('ok');
        }else{
          this.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
        that.close();
      })
    },
    toggle (key) {
      let target = this.data.filter(item => item.key === key)[0]
      target.editable = !target.editable
    },
    getRowByKey (key, newData) {
      const data = this.data
      return (newData || data).filter(item => item.key === key)[0]
    },
    cancel (key) {
      let target = this.data.filter(item => item.key === key)[0]
      target.editable = false
    },
    handleChange (value, key, column) {
      const newData = [...this.data]
      const target = newData.filter(item => key === item.key)[0]
      if (target) {
        target[column] = value
        this.data = newData
      }
    },

    // 最终全页面提交
    validate () {
      this.$refs.repository.form.validateFields((err, values) => {
        if (!err) {
          this.$notification['error']({
            message: 'Received values of form:',
            description: values
          })
        }
      })
      this.$refs.task.form.validateFields((err, values) => {
        if (!err) {
          this.$notification['error']({
            message: 'Received values of form:',
            description: values
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.ant-modal-body {
  padding: 8px!important;
}
</style>