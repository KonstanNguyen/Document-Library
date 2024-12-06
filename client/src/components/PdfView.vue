<template>
  <div ref="pdfContainer"></div>
</template>

<script>
import * as pdfjsLib from "pdfjs-dist";
import * as pdfWorker from "pdfjs-dist/build/pdf.worker.mjs";
import "pdfjs-dist/web/pdf_viewer.css";
import axios from 'axios';

export default {
  props: {
    pdfPath: {
      type: String,
      required: true,
    },
    pageNumber: {
      type: Number,
      default: 5,
    },
  },
  async mounted() {
    pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.8.335/pdf.worker.min.js';

    try {
      const response = await axios.get('http://localhost:5454/pdf/page', {
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
          console.log('PDF loaded, total pages:', pdfDoc.numPages);

          for (let pageNum = 1; pageNum <= pdfDoc.numPages; pageNum++) {
            const page = await pdfDoc.getPage(pageNum);
            console.log('Page loaded:', pageNum);

            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            if (!context) {
              console.error("Không thể lấy context của canvas.");
              continue;
            }

            const viewport = page.getViewport({ scale: 1.5 });
            console.log("Viewport dimensions:", viewport);

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
};
</script>


<style scoped>
div {
  border: 1px solid #ccc;
  width: 100%;
  height: auto;
}
</style>
