<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--      筛选-->
      <!--      分栏24占满 -->
      <el-col :span="24" :xs="24">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="adsList"  @selection-change="handleSelectionChange" >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column width="350px"   label="图片" align="center" key="name" prop="name" v-if="columns[0].visible">
            <template slot-scope="scope" >
              <el-image
                style="width: 325px;height: 150px;border-radius: 5px"
                :src="setUrl(scope.row.url)"
                fit="cover"></el-image>
            </template>
          </el-table-column>
          <el-table-column  label="跳转路径" align="center" key="name" prop="path" v-if="columns[4].visible" />
          <el-table-column label="显示顺序" prop="sort" align="center" key="sort" v-if="columns[1].visible" />
          <el-table-column label="是否禁用" align="center" key="status" v-if="columns[2].visible">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value=1
                :inactive-value=0
                @change="handleStatusChange(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[3].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="160"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope" v-if="scope.row.userId !== 1">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>

          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>
    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="图片" prop="url">
              <el-upload
                action="#"
                list-type="picture-card"
                :file-list="fileList"
                :on-preview="handlePictureCardPreview"
                :httpRequest="httpRequest"
                :limit="1">
                <i slot="default" class="el-icon-plus"></i>
                <div slot="file" slot-scope="{file}">
                  <img
                    class="el-upload-list__item-thumbnail"
                    :src="setUrl(file.url)" alt=""
                  >
                  <span class="el-upload-list__item-actions">
                  <span
                    class="el-upload-list__item-preview"
                    @click="handlePictureCardPreview(file)"
                  >
                    <i class="el-icon-zoom-in"></i>
                  </span>
                  <span
                    class="el-upload-list__item-delete"
                    @click="handleDownload(file)"
                  >
                    <i class="el-icon-download"></i>
                  </span>
                  <span

                    class="el-upload-list__item-delete"
                    @click="handleRemove(file)"
                  >
                    <i class="el-icon-delete"></i>
                  </span>
                </span>
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="跳转路径">
              <el-input v-model="form.path" type="textarea" placeholder="请输入路径"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="form.sort" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="setUrl(dialogImageUrl)" alt="">
    </el-dialog>

  </div>
</template>

<script>
import { addAds, delAds, listAds } from '@/api/ads'
import { upload } from '@/api/talk'
import { getUploadToken, uploadImage } from '@/api/system/common'
import * as qiniu from 'qiniu-js'
import crypto from 'crypto'
export default {
  name: "Ads",
  data() {
    return {
      sys_normal_disable:[
        {
          value:0,
          label:"正常"
        },
        {
          value:1,
          label:"禁用"
        }
      ],
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      adsList: null,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      multiple:true,
      // 日期范围
      dateRange: [],
      // 表单参数
      form: {},
      //选中列表
      ids:[],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name:undefined,
        status: undefined,
      },
      // 列信息
      columns: [
        { key: 0, label: `轮播图图片`, visible: true },
        { key: 1, label: `显示排序`, visible: true },
        { key: 2, label: `状态`, visible: true },
        { key: 3, label: `创建时间`, visible: true },
        { key: 4, label: `跳转路径`, visible: true }
      ],
      // 表单校验
      rules: {
        name: [
          { required: true, message: "轮播图名称不能为空", trigger: "blur" },
          { min: 1, max: 7, message: '轮播图名称长度必须介于 1 和 7 之间', trigger: 'blur' }
        ]
      },
      fileList:[],
      dialogImageUrl:null,
      dialogVisible:false
    };
  },
  watch: {

  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      listAds(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          // console.log(response)
          this.adsList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 状态修改 */
    handleStatusChange(row) {
      let text = row.status === 1 ? "停用" : "启用";
      this.$modal.confirm('确认要'+ text + '轮播图吗？').then(function() {
        return addAds({id:row.id, status:row.status});
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === 0 ? 1 : 0;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /**  表单重置 */
    reset() {
      this.fileList=[]
      this.form = {
        id: undefined,
        url: undefined,
        sort: 0,
        status:0,
        remark:undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.title = "添加轮播图";
      this.open = true;
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.multiple = !selection.length;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const Ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除').then(function() {
        return delAds(Ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.fileList = [{url:row.url}]
      this.form = row;
      this.open = true;
      this.title = "修改轮播图";
    },
    /** 提交按钮 */
    submitForm: function() {
      this.form.url = this.fileList[0].url
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            addAds(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAds(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    //图片删除
    handleRemove(file) {
      this.fileList = this.fileList.filter(item=>item.url!=file.url)
    },
    //图片预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    //图片下载
    handleDownload(file) {


    },
    //图片上传
    async httpRequest({ file }){
      // let md5 = await this.computeFileMD5(file);
      // const name = 'ads/' + md5 + '.' + file.name.split('.').pop()
      // // console.log(name)
      // const  res = await getUploadToken()
      // const config = {
      //   useCdnDomain: true,
      //   region: qiniu.region.z2
      // };
      // const observable = qiniu.upload(file, name, res.data,config)
      // observable.subscribe(
      //   {
      //     next(res){
      //       // ...
      //     },
      //     error(err){
      //       // ...
      //       console.error(err)
      //     },
      //     complete:(res)=>{
      //       // ...
      //       // console.log(res)
      //       this.fileList.push({url:'https://qiniu.xlfdeblog.top/' + res.key})
      //     }
      //   }
      // ) // 上传开始

      const form = new FormData()
      // 文件对象
      form.append('file', file)
      uploadImage(form).then(res => {
        this.fileList.push({url: res.url})
      })
      //阻止默认上传行为
      return false;
    },
    setUrl(url){
      return this.getUrl(url)
    },
    // 计算文件的 MD5 值
    async computeFileMD5(file) {
      const buffer = await this.readFileAsBuffer(file);
      const hash = crypto.createHash("md5");
      hash.update(buffer);
      return hash.digest("hex");
    },

    // 将文件读取为 Buffer
    async readFileAsBuffer(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => {
          resolve(Buffer.from(reader.result));
        };
        reader.onerror = reject;
        reader.readAsArrayBuffer(file);
      });
    },


  }
};
</script>
<style>

</style>
