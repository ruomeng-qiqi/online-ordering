import request from '@/utils/request'

/**
 * 文件上传
 * @param {File} file 文件对象
 * @param {String} type 文件类型（dish-菜品, setmeal-套餐, qrcode-二维码）
 */
export const uploadFile = (file, type) => {
  const formData = new FormData()
  formData.append('file', file)
  if (type) {
    formData.append('type', type)
  }
  
  return request({
    url: '/admin/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
