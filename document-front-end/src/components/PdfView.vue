<template>
  <div class="d-grid">
    <button @click="downloadPdf" class="download-btn">Tải xuống</button>
    <div class="wrapper" ref="pdfContainer"></div>
  </div>
</template>

<script>
import * as pdfjsLib from "pdfjs-dist";
import * as pdfWorker from "pdfjs-dist/build/pdf.worker.mjs";
import "pdfjs-dist/web/pdf_viewer.css";
import apiClient from "@/api/service";

export default {
  props: {
    pdfPath: {
      type: String,
      default: 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf',
    },
    documentId: {
      type: Number,
      required: true,
    },
    pageNumber: {
      type: Number,
      default: 5,
    },
  },
  data() {
    return {
      accountId: localStorage.getItem("userId") || "",
    }
  },
  async mounted() {
    pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.8.335/pdf.worker.min.js';

    try {
      const response = await apiClient.get('/pdf/page', {
        params: {
          pdfPath: this.pdfPath,
          pageNumber: this.pageNumber,
        },
        headers: {
          Accept: 'application/pdf',
        },
        responseType: 'arraybuffer',
      });

      const pdfData = new Uint8Array(response.data);

      const loadingTask = pdfjsLib.getDocument({ data: pdfData });
      loadingTask.promise
        .then(async (pdfDoc) => {

          for (let pageNum = 1; pageNum <= pdfDoc.numPages; pageNum++) {
            const page = await pdfDoc.getPage(pageNum);

            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            if (!context) {
              console.error("Không thể lấy context của canvas.");
              continue;
            }

            const viewport = page.getViewport({ scale: 1.5 });

            canvas.width = viewport.width;
            canvas.height = viewport.height;

            this.$refs.pdfContainer.appendChild(canvas);

            await page.render({
              canvasContext: context,
              viewport: viewport,
            }).promise;
          }
        })
        .catch((error) => {
          console.error("Lỗi khi tải PDF document:", error);
        });
    } catch (error) {
      console.error("Lỗi khi hiển thị trang PDF:", error);
    }
  },
  methods: {
    async downloadPdf() {
      try {
        const pdfPath = this.pdfPath.split('/').pop();
        const response = await apiClient.get('/pdf/download', {
          params: {
            pdfPath: pdfPath,
          },
          responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: 'application/pdf' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = pdfPath;
        link.click();

        await apiClient.post('/api/history-downloads', null, {
          params: {
            accountId: this.accountId,
            documentId: this.$props.documentId,
          },
        });
        console.log("Lịch sử tải xuống đã được lưu");
      } catch (error) {
        console.error("Lỗi khi tải PDF:", error);
      }
    },
  },
};
</script>


<style scoped>
.wrapper {
  border: 1px solid #ccc;
  width: 100%;
  height: auto;
}

.download-btn {
  position: relative;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  top: -117%;
  left: 78%;
  font-size: 18px;
  font-weight: 600;
}

.download-btn:hover {
  background-color: #0056b3;
}
</style>
