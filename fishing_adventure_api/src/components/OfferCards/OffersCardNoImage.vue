<template>
  <div>
    <div
      class="card mb-3 bg-dark mt-3"
      style="width: 61%; margin: auto; background-color: #7e000c3d !important"
    >
      <div class="row g-0">
        <div class="shadow-none" name="main-col">
          <div class="card-body shadow-none">
            <div class="card-text shadow-none" style="display: flex">
              <h5 class="card-title shadow-none">{{ starts }} - {{ ends }}</h5>
              <p class="discountStatus shadow-none">
                <b class="shadow-none"> {{ offer.discount }}% OFF</b>
              </p>
            </div>
            <div
              class="card-text shadow-none"
              style="display: flex; align-items: center"
            >
              <div class="shadow-none" style="width: 100%">
                <div class="row shadow-none">
                  <p class="card-text text-left shadow-none col-md-4">
                    Offer ends in:
                  </p>
                  <p class="card-text text-left shadow-none col-md-4">
                    {{ durationString }}
                  </p>
                </div>
                <div class="row shadow-none">
                  <p class="card-text text-left shadow-none col-md-4">Price:</p>
                  <p class="card-text text-left shadow-none col-md-4">
                     <i class="fas fa-dollar-sign shadow-none" style="font-size: 80%"></i>{{ offer.price  }}
                  </p>
                </div>
                <div class="row shadow-none">
                  <p class="card-text text-left shadow-none col-md-4">
                    Persons:
                  </p>
                  <p class="card-text text-left shadow-none col-md-4">
                    {{ offer.maxPersons }}
                  </p>
                </div>
                <p class="card-text text-left shadow-none">
                  Additional services:
                </p>

                <div class="row shadow-none" style="margin-left: 1%">
                  <p
                    class="additionalServices shadow-none"
                    v-if="offer.chosenServices.length == 0"
                    style="text-align: center"
                  >
                    Not included
                  </p>
                  <p
                    class="additionalServices shadow-none"
                    v-for="service of offer.chosenServices"
                    :key="service.id"
                  >
                    {{ service.name }}
                  </p>
                </div>
              </div>

              <div class="manageReservation shadow-none">
                <button
                  v-if="loggedInRole == 'ROLE_CLIENT'"
                  class="btn btn-outline-primary shadow-none mb-2"
                  style="width: 8rem"
                  v-on:click="createReservation"
                >
                  Book now
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from "moment";
import axios from "axios";
axios.defaults.baseURL = process.env.VUE_APP_URL;
export default {
  props: ["offer", "loggedInRole", "entityType"],
  name: "SpecialOffersCardNoImage",
  data: function () {
    return {
      date: "12/20/2021 - 12/25/2021",
      discount: "50",
      duration: "",
      ends: "",
      starts: "",
      durationString: "",
    };
  },
  mounted() {
    this.ends = new Date(this.offer.endDate);
    this.starts = new Date(this.offer.startDate);
    let offerEnds = this.offer.dateCreated + this.offer.duration;
    this.ends = moment(this.ends).format("MM/DD/yyyy HH:mm");
    this.starts = moment(this.starts).format("MM/DD/yyyy HH:mm");
    let offerDuration = offerEnds - new Date();
    if (this.offer.dateCreated < new Date()) {
      let days = offerDuration / (1000 * 3600 * 24);
      days = parseInt(days, 10);
      let hours = offerDuration / (1000 * 3600) - days * 24;
      hours = parseInt(hours, 10);
      let minutes = offerDuration / (1000 * 60) - days * 24 * 60 - hours * 60;
      minutes = parseInt(minutes, 10);
      this.durationString = days + "d " + hours + "h " + minutes + "m";
    }
  },
  methods: {
    createReservation: function() {
      axios
        .post("/reservation/new/specialOffer/" , 
        this.offer,
        {
          headers: {
            "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
            Authorization: "Bearer " + localStorage.refreshToken,
          },
        })
        .then(() => {
          this.$emit('refresh', this.offer);
          this.$toast.show(
            "Thank you for your reservation!",
            {
              duration: 2000,
            }
          );
        });
    }
  }
};
</script>
<style scoped>
.img-fluid {
  height: 100%;
}
h5 {
  color: white;
}
p {
  margin-bottom: 2%;
  color: gray;
}
@media (min-width: 768px) {
  .col-md-4 {
    flex: 0 0 auto;
    width: 25%;
  }
}
@media (min-width: 768px) {
  .col-md-8 {
    flex: 0 0 auto;
    width: 74.666667%;
  }
}
div {
  text-align: left;
}

.row {
  align-items: center;
}
.card :hover {
  cursor: pointer;
  box-shadow: 2px 2px 15px 2px rgba(187, 178, 178, 0.664);
  transition: 0.5s;
}

.advertiserTitle {
  background-color: rgba(128, 128, 128, 0.226);
  color: gray;
  padding: 1px 5px;
  border-radius: 5px;
  margin-left: 3%;
  width: fit-content;
}

.additionalServices {
  background-color: rgb(0 0 0 / 35%);
  color: gray;
  padding: 1px 5px;
  border-radius: 5px;
  width: fit-content;
  margin: 2px;
}

.manageReservation {
  display: flex;
  flex-direction: column;
  margin-right: 1rem;
}

.card-body {
  padding: 1rem 0 1rem 1rem;
}

.discountStatus {
  margin: auto 0px auto auto;
  text-align: right;
  padding: 0 10px;
  background-color: #b1090966;
  width: 30%;
  text-align: center;
  color: white;
  border-radius: 5px 0px 0px 5px;
  text-decoration: bold;
}

.btn-primary:hover {
  box-shadow: 2px 2px 10px 0px rgb(22 20 20 / 66%) !important;
  transition: 0.5s;
}

.card-text {
  margin: 0;
}
</style>
