<template>
  <div
    class="modal fade dark"
    id="NewCottageModal"
    aria-labelledby="NewCottageModalLabel"
    aria-hidden="true"
    data-bs-backdrop="static"
    data-bs-keyboard="false"
    v-on:show="closeModal"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h3>New reservation</h3>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            v-on:click="closeModal"
          >
            <i class="fas fa-times fa-lg"></i>
          </button>
        </div>
        <div class="modal-body">
          <h6 v-if="!adventure" style="color: white; margin-top: 5%">
            <b>Select date range:</b>
          </h6>
          <h6 v-if="adventure" style="color: white; margin-top: 5%">
            <b>Select date and time:</b>
          </h6>
          <Datepicker
            style="
              width: 100%;
              margin-right: 10px;
              border: 1px solid white;
              border-radius: 5px;
            "
            dark
            id="picker"
            v-if="selectedDateRange == undefined && !adventure"
            v-model="dateRange"
            v-on:click="selectDate"
            range
            :enableTimePicker="true"
            :minDate="new Date()"
          ></Datepicker>

          <Datepicker
            style="
              width: 100%;
              margin-right: 10px;
              border: 1px solid white;
              border-radius: 5px;
            "
            dark
            id="picker"
            v-if="selectedDateRange != undefined && !adventure"
            v-model="selectedDateRange"
            range
            calendar-button="false"
            :enableTimePicker="true"
            :minDate="new Date()"
          ></Datepicker>

          <Datepicker
            style="
              width: 100%;
              margin-right: 10px;
              border: 1px solid white;
              border-radius: 5px;
              text-align: center;
            "
            dark
            id="picker"
            v-if="adventure"
            v-model="selectedDate"
            calendar-button="false"
            :enableTimePicker="true"
          ></Datepicker>

          <h6 style="color: white; margin-top: 10%"><b>Persons:</b></h6>
          <input
            type="number"
            min="1"
            v-if="numOfPersons == undefined"
            v-model="numberOfPersons"
            v-on:click="changePersons"
            style="text-align: center"
            class="form-control"
          />
          <input
            type="number"
            min="1"
            v-if="numOfPersons != undefined"
            v-model="numOfPersons"
            v-on:change="checkPersons"
            style="text-align: center"
            class="form-control"
          />

          <h6 style="color: white; margin-top: 10%">
            <b>Select additional services:</b>
          </h6>
          <div id="table-wrapper" style="margin-top: -5%">
            <div id="table-scroll">
              <table class="table">
                <tbody>
                  <tr
                    v-if="
                      (addServices == undefined || !addServices.length) &&
                      boat != true
                    "
                    style="color: white"
                  >
                    No additional services available
                  </tr>
                  <tr v-if="boat">
                    <td style="color: white">Boat owner</td>
                    <td style="color: white">free</td>
                    <td>
                      <input
                        id="checkBoatOwner"
                        type="checkbox"
                        v-on:click="checkBoatOwner()"
                      />
                    </td>
                  </tr>
                  <tr
                    v-for="additionalService in additionalServices"
                    :key="additionalService.id"
                  >
                    <td>
                      <label style="color: white">{{
                        additionalService.name
                      }}</label>
                    </td>
                    <td>
                      <label style="color: white"
                        >{{ additionalService.price }} $</label
                      >
                    </td>
                    <td>
                      <input
                        :id="additionalService.id"
                        type="checkbox"
                        v-on:click="check(additionalService)"
                      />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <h5 style="color: white; margin-top: 5%" v-if="totalPrice != 0">
            <b>~ Total price: {{ totalPrice }} $ ~</b>
          </h5>
          <h5 style="color: white; margin-top: 5%" v-if="totalPrice == 0">
            <b>~ Total price: {{ price }} $ ~</b>
          </h5>
          <h6 style="color: red">
            <b>{{ error }}</b>
          </h6>
        </div>
        <div class="modal-footer steps-div">
          <button
            type="button"
            class="btn btn-outline-primary"
            v-on:click="createReservation"
          >
            Create
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
axios.defaults.baseURL = process.env.VUE_APP_URL;
import moment from "moment";
export default {
  components: {},
  props: [
    "duration",
    "serviceId",
    "date",
    "persons",
    "additionalServices",
    "price",
  ],
  name: "RegisterModal",
  data: function () {
    return {
      addServices: [],
      dateRange: [],
      selectedDateRange: undefined,
      selectedDate: new Date(),
      numberOfPersons: 1,
      numOfPersons: undefined,
      totalPrice: 0,
      active: false,
      chosenServices: [],
      maxPersons: 0,
      availableForDateRange: true,
      adventureAvailability: true,
      cancelled: false,
      boat: false,
      boatOwner: false,
      boatOwnerAvailable: true,
      adventure: false,
      error: "",
    };
  },
  mounted: function () {
    var element = document.getElementById("logIn-btn");
    element.classList.add("active");
    this.addServices = this.additionalServices;
    this.boatOwnerAvailable = true;
  },
  beforeUpdate: function () {
    this.addServices = this.additionalServices;
    if (window.location.href.includes("boat")) this.boat = true;
    if (window.location.href.includes("adventure")) this.adventure = true;

    if (this.date !== "undefined" && !this.adventure) {
      this.dateRange.push(
        moment(new Date(Date.parse(this.date.split(",")[0]))).subtract(
          1,
          "hours"
        )
      );
      this.dateRange.push(
        moment(new Date(Date.parse(this.date.split(",")[1]))).subtract(
          1,
          "hours"
        )
      );
    } else {
      if (this.dateRange == null) {
        this.dateRange = [];
      }
      this.dateRange.push(moment(new Date()));
      this.dateRange.push(moment(new Date()).add(7, "days"));
    }
    if (this.persons !== "undefined" && !this.adventure) {
      if (this.persons != 0) this.numberOfPersons = this.persons;
    }

    if (!this.adventure) {
      this.totalPrice = Math.round(
        ((this.dateRange[1] - this.dateRange[0]) / (1000 * 3600 * 24)) *
          this.$props.price
      );
    } else {
      this.totalPrice = this.$props.price;
    }
    for (let as of this.chosenServices) {
      this.totalPrice += as.price;
    }

    if (this.selectedDateRange != undefined && !this.adventure) {
      this.totalPrice = Math.round(
        ((this.selectedDateRange[1] - this.selectedDateRange[0]) /
          (1000 * 3600 * 24)) *
          this.$props.price
      );
    }
    for (let as of this.chosenServices) {
      this.totalPrice += as.price;
    }
  },
  methods: {
    checkBoatOwner: function () {
      var checkBox = document.getElementById("checkBoatOwner");
      if (checkBox.checked === true) {
        this.boatOwner = true;
        this.boatOwnerAvailable = false;
      } else {
        this.boatOwner = false;
        this.boatOwnerAvailable = true;
      }
    },
    check: function (additionalService) {
      var checkBox = document.getElementById(additionalService.id);
      if (checkBox.checked === true) {
        this.chosenServices.push(additionalService);
      } else {
        const index = this.chosenServices.indexOf(additionalService);
        if (index > -1) {
          this.chosenServices.splice(index, 1);
        }
      }

      this.totalPrice = Math.round(
        ((this.dateRange[1] - this.dateRange[0]) / (1000 * 3600 * 24)) *
          this.$props.price
      );
      for (let as of this.chosenServices) {
        this.totalPrice += as.price;
      }
    },
    selectDate: function () {
      if (this.date != undefined) {
        this.selectedDateRange = this.dateRange;
      } else {
        if (this.dateRange == null) {
          this.dateRange = [];
        }
        this.dateRange.push(new Date());
        this.dateRange.push(new Date() + 7);
      }
    },
    changeDateRange: function () {
      if (this.selectedDateRange == null) return;
      axios
        .get(
          "/vacationHome/available/dateRange?id=" +
            this.serviceId +
            "&start=" +
            moment(this.selectedDateRange[0]).format(
              "yyyy-MM-DD HH:mm:ss.SSS"
            ) +
            "&end=" +
            moment(this.selectedDateRange[1]).format("yyyy-MM-DD HH:mm:ss.SSS"),
          {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          }
        )
        .then((response) => {
          this.availableForDateRange = response.data;
        });
    },
    changePersons: function () {
      this.numOfPersons = parseInt(this.numberOfPersons);
      this.checkPersons();
    },
    checkPersons: function () {
      if (window.location.href.includes("cottage")) {
        this.checkPersonsForCottage();
      } else if (window.location.href.includes("boat")) {
        this.checkPersonsForBoat();
      } else {
        this.checkPersonsForAdventure();
      }
    },
    checkPersonsForCottage: function () {
      axios
        .get("/vacationHome/persons?id=" + this.serviceId, {
          headers: {
            "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
            Authorization: "Bearer " + localStorage.refreshToken,
          },
        })
        .then((response) => {
          this.maxPersons = response.data;
        });
    },
    checkPersonsForBoat: function () {
      axios
        .get("/boat/persons?id=" + this.serviceId, {
          headers: {
            "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
            Authorization: "Bearer " + localStorage.refreshToken,
          },
        })
        .then((response) => {
          this.maxPersons = response.data;
        });
    },
    checkPersonsForAdventure: function () {
      axios
        .get("/fishingAdventure/persons?id=" + this.serviceId, {
          headers: {
            "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
            Authorization: "Bearer " + localStorage.refreshToken,
          },
        })
        .then((response) => {
          this.maxPersons = response.data;
        });
    },
    createReservation: function () {
      if (this.selectedDateRange == undefined) this.selectDate();

      if (this.numOfPersons == undefined) this.changePersons();

      if (this.totalPrice == 0) this.totalPrice = this.price;

      this.error = "";

      if (this.selectedDateRange == null && !this.adventure) {
        this.error = "Select date range please";
        return;
      }

      if (window.location.href.includes("cottage")) {
        this.checkCottageAvailability();
      } else if (window.location.href.includes("boat")) {
        this.checkBoatAvailability();
      } else {
        this.checkAdventureAvailability();
      }
    },
    checkCottageAvailability: function () {
      axios
        .get(
          "/vacationHome/available/dateRange?id=" +
            this.serviceId +
            "&start=" +
            moment(this.selectedDateRange[0]).format(
              "yyyy-MM-DD HH:mm:ss.SSS"
            ) +
            "&end=" +
            moment(this.selectedDateRange[1]).format("yyyy-MM-DD HH:mm:ss.SSS"),
          {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          }
        )
        .then((response) => {
          this.availableForDateRange = response.data;
        })
        .finally(() => {
          this.checkClientReservations();
        });
    },
    checkBoatAvailability: function () {
      axios
        .get(
          "/boat/available/dateRange?id=" +
            this.serviceId +
            "&start=" +
            moment(this.selectedDateRange[0]).format(
              "yyyy-MM-DD HH:mm:ss.SSS"
            ) +
            "&end=" +
            moment(this.selectedDateRange[1]).format("yyyy-MM-DD HH:mm:ss.SSS"),
          {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          }
        )
        .then((response) => {
          this.availableForDateRange = response.data;
          if (this.boatOwner == true) {
            axios
              .get(
                "/boatOwner/available/dateRange?id=" +
                  this.serviceId +
                  "&start=" +
                  moment(this.selectedDateRange[0]).format(
                    "yyyy-MM-DD HH:mm:ss.SSS"
                  ) +
                  "&end=" +
                  moment(this.selectedDateRange[1]).format(
                    "yyyy-MM-DD HH:mm:ss.SSS"
                  ),
                {
                  headers: {
                    "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
                    Authorization: "Bearer " + localStorage.refreshToken,
                  },
                }
              )
              .then((res) => (this.boatOwnerAvailable = res.data));
          }
        })
        .finally(() => {
          this.checkClientReservations();
        });
    },
    checkAdventureAvailability: function () {
      let startDate = this.selectedDate;
      let endDate = new Date(startDate.getTime() + this.duration * 60000);
      axios
        .get(
          "/fishingAdventure/isAdventureInstructorAvailable?id=" +
            this.serviceId +
            "&start=" +
            moment(startDate).format("yyyy-MM-DD HH:mm:ss.SSS") +
            "&end=" +
            moment(endDate).format("yyyy-MM-DD HH:mm:ss.SSS"),
          {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          }
        )
        .then((res) => {
          this.adventureAvailability = res.data;
          this.checkClientReservations();
        });
    },
    checkClientReservations: function () {
      let startDate = {};
      let endDate = {};

      if (window.location.href.includes("adventure")) {
        startDate = this.selectedDate;
        endDate = new Date(startDate.getTime() + this.duration * 60000);
      } else {
        startDate = this.selectedDateRange[0];
        endDate = this.selectedDateRange[1];
      }

      axios
        .get(
          "/reservation/cancelled?serviceId=" +
            this.serviceId +
            "&start=" +
            moment(startDate).format("yyyy-MM-DD HH:mm:ss.SSS") +
            "&end=" +
            moment(endDate).format("yyyy-MM-DD HH:mm:ss.SSS"),
          {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          }
        )
        .then((res) => {
          if (res.data) {
            this.cancelled = true;
          } else {
            this.cancelled = false;
          }
          this.saveReservation();
        });
    },
    saveReservation: function () {
      if (
        this.maxPersons >= this.numOfPersons &&
        this.availableForDateRange &&
        this.boatOwnerAvailable &&
        !this.cancelled &&
        this.adventureAvailability
      ) {
        let startDate = {};
        let endDate = {};

        if (window.location.href.includes("adventure")) {
          startDate = this.selectedDate;
          endDate = new Date(startDate.getTime() + this.duration * 60000);
        } else {
          startDate = this.selectedDateRange[0];
          endDate = this.selectedDateRange[1];
        }

        let reservation = {
          serviceId: this.serviceId,
          startDate: startDate,
          endDate: endDate,
          persons: this.numOfPersons,
          chosenServices: this.chosenServices,
          price: this.totalPrice,
          ownersPresence: this.boatOwner,
        };

        axios
          .post("/reservation/new", reservation, {
            headers: {
              "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
              Authorization: "Bearer " + localStorage.refreshToken,
            },
          })
          .then((res) => {
            window.location.reload();
            if (!res.data)
              this.$toast.show("Something went wrong, please check your penalties and try again");
          });
      } else {
        if (this.maxPersons < this.numOfPersons)
          this.error = "Maximum number of people is " + this.maxPersons + ".";
        else if (!this.availableForDateRange)
          this.error = "Not available for selected date range!";
        else if (!this.adventureAvailability)
          this.error = "Not available for selected date!";
        else if (!this.boatOwnerAvailable)
          this.error = "Boat owner is not available";
        else this.error = "Already cancelled reservation in this period!";
      }
    },
    closeModal: function () {
      this.mode = "1";
      if (!this.cottage) {
        this.cottageName = "";
        this.cottageDescription = "";
        this.images = [];
        this.street = "";
        this.city = "";
        this.country = "";
        this.rooms = [];
        this.rules = [];
        this.priceList = [];
      }
      let container = document.getElementsByClassName("pac-container")[0];
      if (container) {
        container.remove();
      }
    },
  },
};
</script>

<style scoped src="@/css/newCottageModal.css"></style>
