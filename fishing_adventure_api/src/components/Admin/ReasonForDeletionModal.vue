<template>
  <div
    class="modal fade dark"
    :id="id"
    tabindex="-1"
    aria-labelledby="OfferModalLabel"
    data-bs-backdrop="static"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content" style="height: 20.5rem">
        <div class="modal-header">
          <h3 style="font-size: 23px;">
            Response
          </h3>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            v-on:click="closeModal"
          >
            <i class="fas fa-times fa-lg"></i>
          </button>
        </div>
        <div
          class="modal-body"
        >
          <textarea
            class="login-inputs-textarea"
            placeholder="Enter respone here.."
            v-model="response"
            style="max-height: 10rem; margin-top:20px"
          />

          <h6 style="color: red; margin-top: 1rem">
            <b>{{ error }}</b>
          </h6>
        </div>
       
        <div class="modal-footer">
          <button
          
            type="button"
            class="btn btn-outline-primary"
            style="width: 20%"
            v-on:click="approveDeletion"
          >
            Approve
          </button>
          <button
            type="button"
            class="btn btn-outline-primary"
            style="width: 20%"
            v-on:click="rejectDeletion"
          >
            Reject
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
axios.defaults.baseURL = process.env.VUE_APP_URL;
export default {
    name: "RequestForDeletion",
    props: ['id', 'request'],
    data: function () {
        return {
          response: "",
          error: "",
        };
    },
    mounted: function () {
    },
    methods: {
        closeModal: function () {
        this.response = "";
        this.error = "";
      },
      approveDeletion: function () {
          if (this.response == "") {
              this.error = "You must write a response.";
          }
          else {
              axios
                  .get(
                  "/deleteRequest/approveDeletion?id=" +
                      this.request.id + "&response=" + this.response,
                  {
                      headers: {
                      "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
                      Authorization: "Bearer " + localStorage.refreshToken,
                      },
                  }
                  )
                  .then(window.location.reload());
          }
      },
      rejectDeletion: function () {
          if (this.response == "") {              
            this.error = "You must write a response.";

          }
          else {
              axios
                  .get(
                  "/deleteRequest/rejectDeletion?id=" +
                      this.request.id + "&response=" + this.response,
                  {
                      headers: {
                      "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
                      Authorization: "Bearer " + localStorage.refreshToken,
                      },
                  }
                  )
                  .then(window.location.reload());
          }
      },
    },
};
</script>


<style scoped src="@/css/newCottageModal.css"></style>
