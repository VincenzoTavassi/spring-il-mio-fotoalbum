const { createApp } = Vue;

createApp({
  data() {
    return {
      apiUrl: "http://localhost:8080/api/v1/",
      photos: [],
      email: "",
      message: "",
      keyword: "",
      success: false,
      errors: false,
      errorMessages: [],
    };
  },
  methods: {
    async fetchPhotos() {
      try {
        const response = await axios.get(this.apiUrl + "foto");
        this.photos = response.data;
      } catch (error) {
        console.log(error);
      }
    },
    async fetchPhotosByKeyword() {
      try {
        const response = await axios.get(
          this.apiUrl + "foto?keyword=" + this.keyword
        );
        this.photos = response.data;
      } catch (error) {
        console.log(error);
      }
    },
    async sendMessage() {
      this.errors = false;
      try {
        await axios
          .post(this.apiUrl + "messages/create", {
            email: this.email,
            text: this.message,
          })
          .then((response) => {
            if (response.status == 200) this.sentMessage();
            else this.errors = true;
          });
      } catch (error) {
        this.errors = true;
        this.errorMessages = error.response.data.errors;
      }
    },
    sentMessage() {
      (this.email = ""), (this.message = "");
      this.success = true;
      setTimeout(() => {
        this.success = false;
      }, 2000);
    },
  },
  mounted() {
    this.fetchPhotos();
  },
}).mount("#app");
