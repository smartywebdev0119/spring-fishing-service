package isa.FishingAdventure.service;

import isa.FishingAdventure.dto.AdvertiserReservationDto;
import isa.FishingAdventure.model.*;
import isa.FishingAdventure.repository.ReservationRepository;
import isa.FishingAdventure.security.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private FishingInstructorService instructorService;

    @Autowired
    private VacationHomeOwnerService homeOwnerService;

    @Autowired
    private BoatOwnerService boatOwnerService;

    @Autowired
    private FishingAdventureService adventureService;

    @Autowired
    private VacationHomeService vacationHomeService;

    @Autowired
    private BoatService boatService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ServiceProfileService serviceProfileService;

    @Autowired
    private EmailService emailService;

    public void save(Reservation reservation) {
        repository.save(reservation);
    }

    public List<Reservation> findAll() throws AccessDeniedException {
        return repository.findAll();
    }

    public boolean createReservation(String clientEmail, Appointment newAppointment, Integer serviceProfileId) {
        try {
            Client client = clientService.findByEmail(clientEmail);
            appointmentService.save(newAppointment);
            ServiceProfile serviceProfile = serviceProfileService.getById(serviceProfileId);
            serviceProfile.getAppointments().add(newAppointment);
            serviceProfileService.save(serviceProfile);
            Reservation newReservation = new Reservation();
            newReservation.setReportFilled(false);
            newReservation.setAppointment(newAppointment);
            newReservation.setClient(client);
            newReservation.setCanceled(false);
            save(newReservation);
            String emailText = createEmail(client, newAppointment, serviceProfile);
            emailService.sendEmail(clientEmail, "Reservation confirmation", emailText);

        } catch (Exception e) {
            return true;
        }

        return true;
    }

    public Reservation getById(Integer id) {
        return repository.getById(id);
    }

    public List<Reservation> getClientCurrentReservations(String token) {
        String email = tokenUtils.getEmailFromToken(token.split(" ")[1]);
        Client client = clientService.findByEmail(email);
        List<Reservation> reservations = repository.findByClient(client);
        List<Reservation> currentReservations = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCanceled().equals(true))
                continue;
            if (r.getAppointment().getEndDate().after(new Date())) {
                currentReservations.add(r);
            }
        }
        return currentReservations;
    }

    public List<ServiceProfile> getServiceProfiles(List<Reservation> reservations) {
        List<ServiceProfile> serviceProfiles = new ArrayList<ServiceProfile>();
        for (Reservation r : reservations) {
            if (r.getCanceled().equals(true))
                continue;
            for (ServiceProfile s : serviceProfileService.findAll()) {
                if (s.getAppointments().contains(r.getAppointment())) {
                    serviceProfiles.add(s);
                    break;
                }
            }
        }
        return serviceProfiles;
    }

    public List<Reservation> getClientPastReservations(String token) {
        String email = tokenUtils.getEmailFromToken(token.split(" ")[1]);
        Client client = clientService.findByEmail(email);
        List<Reservation> reservations = repository.findByClient(client);
        List<Reservation> currentReservations = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCanceled().equals(true))
                continue;
            if (r.getAppointment().getEndDate().before(new Date())) {
                currentReservations.add(r);
            }
        }
        return currentReservations;
    }

    public List<AdvertiserReservationDto> findAllReservationsByAdvertiser(String email, String role) {
        List<AdvertiserReservationDto> reservationDtos = new ArrayList<>();
        if (role.equals("ROLE_FISHING_INSTRUCTOR"))
            reservationDtos = findAllReservationsForInstructor(email);
        else if (role.equals("ROLE_VACATION_HOME_OWNER"))
            reservationDtos = findAllReservationsForHomeOwner(email);
        else
            reservationDtos = findAllReservationsForBoatOwner(email);

        return reservationDtos;
    }

    private List<AdvertiserReservationDto> findAllReservationsForInstructor(String email) {
        FishingInstructor instructor = instructorService.findByEmail(email);
        List<AdvertiserReservationDto> reservationDtos = new ArrayList<>();
        for (FishingAdventure adventure : adventureService.findByFishingInstructor(instructor)) {
            List<AdvertiserReservationDto> resDtos = getReservationsFromAppointments(adventure.getAppointments());
            for (AdvertiserReservationDto reservationDto : resDtos) {
                reservationDto.setName(adventure.getName());
                for (Image img : adventure.getImages()) {
                    if (img.isCoverImage()) {
                        reservationDto.setImagePath(img.getPath());
                        break;
                    }
                }
                reservationDto
                        .setStatus(checkReservationStatus(reservationDto.getIsReportFilled(),
                                reservationDto.getStartDate(), reservationDto.getEndDate()));
            }
            reservationDtos.addAll(resDtos);
        }
        return reservationDtos;
    }

    private List<AdvertiserReservationDto> findAllReservationsForHomeOwner(String email) {
        VacationHomeOwner homeOwner = homeOwnerService.findByEmail(email);
        List<AdvertiserReservationDto> reservationDtos = new ArrayList<>();
        for (VacationHome home : vacationHomeService.findByVacationHomeOwner(homeOwner)) {
            List<AdvertiserReservationDto> resDtos = getReservationsFromAppointments(home.getAppointments());
            for (AdvertiserReservationDto reservationDto : resDtos) {
                reservationDto.setName(home.getName());
                for (Image img : home.getImages()) {
                    if (img.isCoverImage()) {
                        reservationDto.setImagePath(img.getPath());
                        break;
                    }
                }
                reservationDto
                        .setStatus(checkReservationStatus(reservationDto.getIsReportFilled(),
                                reservationDto.getStartDate(), reservationDto.getEndDate()));
            }
            reservationDtos.addAll(resDtos);
        }
        return reservationDtos;
    }

    private List<AdvertiserReservationDto> findAllReservationsForBoatOwner(String email) {
        BoatOwner boatOwner = boatOwnerService.findByEmail(email);
        List<AdvertiserReservationDto> reservationDtos = new ArrayList<>();
        for (Boat boat : boatService.findByBoatOwner(boatOwner)) {
            List<AdvertiserReservationDto> resDtos = getReservationsFromAppointments(boat.getAppointments());
            for (AdvertiserReservationDto reservationDto : resDtos) {
                reservationDto.setName(boat.getName());
                for (Image img : boat.getImages()) {
                    if (img.isCoverImage()) {
                        reservationDto.setImagePath(img.getPath());
                        break;
                    }
                }
                reservationDto
                        .setStatus(checkReservationStatus(reservationDto.getIsReportFilled(),
                                reservationDto.getStartDate(), reservationDto.getEndDate()));
            }
            reservationDtos.addAll(resDtos);
        }
        return reservationDtos;
    }

    private List<AdvertiserReservationDto> getReservationsFromAppointments(Set<Appointment> appointments) {
        List<AdvertiserReservationDto> reservationDtos = new ArrayList<>();
        for (Appointment appointment : appointments) {
            for (Reservation reservation : findAll()) {
                if (appointment.getAppointmentId().equals(reservation.getAppointment().getAppointmentId())) {
                    AdvertiserReservationDto reservationDto = new AdvertiserReservationDto(reservation);
                    reservationDtos.add(reservationDto);
                }
            }
        }

        return reservationDtos;
    }

    public boolean isReservationCanceled(int appointmentId) {
        boolean isCanceled = false;
        for (Reservation reservation : findAll()) {
            if (reservation.getAppointment().getAppointmentId().equals(appointmentId)) {
                if (reservation.getCanceled()) {
                    isCanceled = true;
                    break;
                }
            }
        }
        return isCanceled;
    }

    private String checkReservationStatus(Boolean isReportFilled, Date startDate, Date endDate) {
        String reservationStatus = "";
        Date today = new Date(System.currentTimeMillis());
        if (startDate.after(today))
            reservationStatus = "Upcoming";
        else if (endDate.before(today) && !isReportFilled)
            reservationStatus = "Finished";
        else if (endDate.before(today) && isReportFilled)
            reservationStatus = "Report filled";
        else
            reservationStatus = "Current";

        return reservationStatus;
    }

    public void reserveSpecialOffer(String email, Integer offerId) {
        Client client = clientService.findByEmail(email);
        Appointment appointment = appointmentService.findById(offerId);
        appointment.setReserved(true);
        appointmentService.save(appointment);
        Reservation newReservation = new Reservation();
        newReservation.setReportFilled(false);
        newReservation.setClient(client);
        newReservation.setAppointment(appointment);
        newReservation.setCanceled(false);
        save(newReservation);
    }

    public Reservation findById(Integer id) {
        return repository.findById(id).orElse(new Reservation());
    }

    private String createEmail(Client client, Appointment newAppointment, ServiceProfile serviceProfile) {
        StringBuilder content = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        content.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                        +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:'Open Sans', sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n"
                        +
                        " <head> \n" +
                        "  <meta charset=\"UTF-8\"> \n" +
                        "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"> \n" +
                        "  <meta name=\"x-apple-disable-message-reformatting\"> \n" +
                        "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                        "  <meta content=\"telephone=no\" name=\"format-detection\"> \n" +
                        "  <title>New message 2</title> \n" +
                        "  <!--[if (mso 16)]>\n" +
                        "    <style type=\"text/css\">\n" +
                        "    a {text-decoration: none;}\n" +
                        "    </style>\n" +
                        "    <![endif]--> \n" +
                        "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> \n" +
                        "  <!--[if !mso]><!-- --> \n" +
                        "  <link href=\"https://fonts.googleapis.com/css?family=Oswald:300,700&display=swap\" rel=\"stylesheet\"> \n"
                        +
                        "  <!--<![endif]--> \n" +
                        "  <!--[if gte mso 9]>\n" +
                        "<xml>\n" +
                        "    <o:OfficeDocumentSettings>\n" +
                        "    <o:AllowPNG></o:AllowPNG>\n" +
                        "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                        "    </o:OfficeDocumentSettings>\n" +
                        "</xml>\n" +
                        "<![endif]--> \n" +
                        "  <!--[if !mso]><!-- --> \n" +
                        "  <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i\" rel=\"stylesheet\"> \n"
                        +
                        "  <!--<![endif]--> \n" +
                        "  <style type=\"text/css\">\n" +
                        "#outlook a {\n" +
                        "\tpadding:0;\n" +
                        "}\n" +
                        ".ExternalClass {\n" +
                        "\twidth:100%;\n" +
                        "}\n" +
                        ".ExternalClass,\n" +
                        ".ExternalClass p,\n" +
                        ".ExternalClass span,\n" +
                        ".ExternalClass font,\n" +
                        ".ExternalClass td,\n" +
                        ".ExternalClass div {\n" +
                        "\tline-height:100%;\n" +
                        "}\n" +
                        ".es-button {\n" +
                        "\tmso-style-priority:100!important;\n" +
                        "\ttext-decoration:none!important;\n" +
                        "}\n" +
                        "a[x-apple-data-detectors] {\n" +
                        "\tcolor:inherit!important;\n" +
                        "\ttext-decoration:none!important;\n" +
                        "\tfont-size:inherit!important;\n" +
                        "\tfont-family:inherit!important;\n" +
                        "\tfont-weight:inherit!important;\n" +
                        "\tline-height:inherit!important;\n" +
                        "}\n" +
                        ".es-desk-hidden {\n" +
                        "\tdisplay:none;\n" +
                        "\tfloat:left;\n" +
                        "\toverflow:hidden;\n" +
                        "\twidth:0;\n" +
                        "\tmax-height:0;\n" +
                        "\tline-height:0;\n" +
                        "\tmso-hide:all;\n" +
                        "}\n" +
                        "[data-ogsb] .es-button {\n" +
                        "\tborder-width:0!important;\n" +
                        "\tpadding:25px 40px 25px 40px!important;\n" +
                        "}\n" +
                        "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120% } h1 { font-size:28px!important; text-align:left } h2 { font-size:20px!important; text-align:left } h3 { font-size:14px!important; text-align:left } h1 a { text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:28px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:20px!important } h3 a { text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:14px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:14px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:14px!important; display:block!important; border-bottom-width:20px!important; border-right-width:0px!important; border-left-width:0px!important; border-top-width:20px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }\n"
                        +
                        "</style> \n" +
                        " </head> \n" +
                        " <body style=\"width:100%;font-family:'Open Sans', sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"> \n"
                        +
                        "  <div class=\"es-wrapper-color\" style=\"background-color:#F5F5F5\"> \n" +
                        "   <!--[if gte mso 9]>\n" +
                        "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                        "\t\t\t\t<v:fill type=\"tile\" color=\"#f5f5f5\"></v:fill>\n" +
                        "\t\t\t</v:background>\n" +
                        "\t\t<![endif]--> \n" +
                        "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\"> \n"
                        +
                        "     <tr style=\"border-collapse:collapse\"> \n" +
                        "      <td valign=\"top\" style=\"padding:0;Margin:0\"> \n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#1B2A2F;background-repeat:repeat;background-position:center top\"> \n"
                        +
                        "         <tr style=\"border-collapse:collapse\"> \n" +
                        "          <td align=\"center\" bgcolor=\"#111517\" style=\"padding:0;Margin:0;background-color:#111517\"> \n"
                        +
                        "           <table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#111517\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#111517;width:590px\"> \n"
                        +
                        "             <tr style=\"border-collapse:collapse\"> \n" +
                        "              <td align=\"left\" style=\"padding:0;Margin:0;padding-top:25px;padding-bottom:40px\"> \n"
                        +
                        "               <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                 <tr style=\"border-collapse:collapse\"> \n" +
                        "                  <td class=\"es-m-p0r\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:590px\"> \n"
                        +
                        "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                     <tr style=\"border-collapse:collapse\"> \n" +
                        "                      <td align=\"center\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;padding-top:20px;font-size:0px\"><a target=\"_blank\" href=\"https://viewstripo.email/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#EF0D33;font-size:14px\"><img src=\"https://kkplso.stripocdn.email/content/guids/CABINET_ec454b5835c81fbe0db2fe3eeb9ff2c6/images/logo_white.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"240\" height=\"98\"></a></td> \n"
                        +
                        "                     </tr> \n" +
                        "                   </table></td> \n" +
                        "                 </tr> \n" +
                        "               </table></td> \n" +
                        "             </tr> \n" +
                        "           </table></td> \n" +
                        "         </tr> \n" +
                        "       </table> \n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"> \n"
                        +
                        "         <tr style=\"border-collapse:collapse\"> \n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                        "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#F5F5F5;width:590px\"> \n"
                        +
                        "             <tr style=\"border-collapse:collapse\"> \n" +
                        "              <td align=\"left\" style=\"Margin:0;padding-left:15px;padding-right:15px;padding-top:40px;padding-bottom:40px\"> \n"
                        +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                 <tr style=\"border-collapse:collapse\"> \n" +
                        "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\"> \n"
                        +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                     <tr style=\"border-collapse:collapse\"> \n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:34px;mso-line-height-rule:exactly;font-family:Oswald, sans-serif;font-size:28px;font-style:normal;font-weight:bold;color:#262626\">Thank you for reservation!</h1></td> \n"
                        +
                        "                     </tr> \n" +
                        "                     <tr style=\"border-collapse:collapse\"> \n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0\"><br><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#262626;font-size:14px\">You can cancel your reservation up to 3 days before it is scheduled.</p></td> \n"
                        +
                        "                     </tr> \n" +
                        "                   </table></td> \n" +
                        "                 </tr> \n" +
                        "               </table></td> \n" +
                        "             </tr> \n" +
                        "             <tr style=\"border-collapse:collapse\"> \n" +
                        "              <td class=\"esdev-adapt-off\" align=\"left\" bgcolor=\"#8b9eac\" style=\"Margin:0;padding-bottom:15px;padding-left:15px;padding-right:15px;padding-top:20px;background-color:#8b9eac\"> \n"
                        +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:560px\"> \n"
                        +
                        "                 <tr style=\"border-collapse:collapse\"> \n" +
                        "                  <td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\"> \n" +
                        "                   <table cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                     <tr style=\"border-collapse:collapse\"> \n" +
                        "                      <td align=\"left\" style=\"padding:0;Margin:0;width:1000px\"> \n" +
                        "                       <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                        +
                        "                         <tr style=\"border-collapse:collapse\"> \n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0;padding-bottom:10px\"> \n"
                        +
                        "                           <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%\" class=\"cke_show_border\" cellspacing=\"1\" cellpadding=\"1\" border=\"0\" role=\"presentation\"> \n"
                        +
                        "                             <tr style=\"border-collapse:collapse\"> \n" +
                        "                              <td align=\"center\" style=\"padding:0;Margin:0\"><strong>");
        content.append(serviceProfile.getName());
        content.append(
                "</strong><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#262626;font-size:14px\"><br></p></td> \n"
                        +
                        "                             </tr> \n" +
                        "                           </table></td> \n" +
                        "                         </tr> \n" +
                        "                         <tr style=\"border-collapse:collapse\"> \n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0\"><br></td> \n" +
                        "                         </tr> \n" +
                        "                         <tr style=\"border-collapse:collapse\"> \n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">Reservation for: ");
        content.append(serviceProfile.getName());
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">Location: ");
        content.append(serviceProfile.getLocation().getAddress().getStreet());
        content.append(", ").append(serviceProfile.getLocation().getAddress().getCity());
        content.append(", ").append(serviceProfile.getLocation().getAddress().getCountry());
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">Start date: ");
        content.append(format.format(newAppointment.getStartDate()));
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">End date: ");
        content.append(format.format(newAppointment.getEndDate()));
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">Chosen additional services: ");
        for (AdditionalService as : newAppointment.getChosenServices()) {
            content.append("<tr>\n" +
                    "                                                <td align=\"left\" class=\"esd-block-text\">\n" +
                    "                                                    <p style=\"color: #333333;\">");
            content.append("\t").append(as.getName()).append(" ").append(as.getPrice()).append("$");
            content.append("</p>\n" +
                    "                                                </td>\n" +
                    "                                            </tr>");

        }
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:21px;color:#333333;font-size:14px\">Total price: ");
        content.append(newAppointment.getPrice());
        content.append("</p></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;background-position:center center\"> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:590px\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" style=\"Margin:0;padding-top:5px;padding-bottom:5px;padding-left:15px;padding-right:15px;font-size:0\"> \n"
                +
                "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td style=\"padding:0;Margin:0;border-bottom:1px solid #666666;background:none;height:1px;width:100%;margin:0px\"></td> \n"
                +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#111517;background-repeat:repeat;background-position:center top\"> \n"
                +
                "         <tr style=\"border-collapse:collapse\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\"> \n" +
                "           <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#111517;width:590px\"> \n"
                +
                "             <tr style=\"border-collapse:collapse\"> \n" +
                "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;padding-bottom:40px\"> \n"
                +
                "               <!--[if mso]><table style=\"width:550px\" cellpadding=\"0\" \n" +
                "                        cellspacing=\"0\"><tr><td style=\"width:170px\" valign=\"top\"><![endif]--> \n"
                +
                "               <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"> \n"
                +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td class=\"es-m-p20b\" align=\"left\" style=\"padding:0;Margin:0;width:170px\"> \n"
                +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://viewstripo.email/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#EF0D33;font-size:12px\"><img src=\"https://kkplso.stripocdn.email/content/guids/CABINET_ec454b5835c81fbe0db2fe3eeb9ff2c6/images/logo_white_XSy.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"170\" height=\"69\"></a></td> \n"
                +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"center\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;padding-top:20px;font-size:0\"> \n"
                +
                "                       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-table-not-adapt es-social\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                         <tr style=\"border-collapse:collapse\"> \n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:10px\"><img src=\"https://kkplso.stripocdn.email/content/assets/img/social-icons/logo-white/facebook-logo-white.png\" alt=\"Fb\" title=\"Facebook\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></td> \n"
                +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:10px\"><img src=\"https://kkplso.stripocdn.email/content/assets/img/social-icons/logo-white/twitter-logo-white.png\" alt=\"Tw\" title=\"Twitter\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></td> \n"
                +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:10px\"><img src=\"https://kkplso.stripocdn.email/content/assets/img/social-icons/logo-white/youtube-logo-white.png\" alt=\"Yt\" title=\"Youtube\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></td> \n"
                +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0\"><img src=\"https://kkplso.stripocdn.email/content/assets/img/social-icons/logo-white/instagram-logo-white.png\" alt=\"Ig\" title=\"Instagram\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></td> \n"
                +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:360px\" valign=\"top\"><![endif]--> \n"
                +
                "               <table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\"> \n"
                +
                "                 <tr style=\"border-collapse:collapse\"> \n" +
                "                  <td align=\"left\" style=\"padding:0;Margin:0;width:360px\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"> \n"
                +
                "                     <tr style=\"border-collapse:collapse\"> \n" +
                "                      <td align=\"left\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;padding-top:20px;padding-bottom:20px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'Open Sans', sans-serif;line-height:18px;color:#BCBDBD;font-size:12px\">We are students at the Faculty of Technical Sciences in Novi Sad trying to make this project for a course in Internet Software Architecture.</p></td> \n"
                +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table></td> \n" +
                "     </tr> \n" +
                "   </table> \n" +
                "  </div>  \n" +
                " </body>\n" +
                "</html>");
        return content.toString();
    }

    public boolean cancelReservation(Integer id) {
        for (Reservation r : findAll()) {
            if (r.getReservationId().equals(id)) {
                r.setCanceled(true);
                r.getAppointment().setCancelled(true);
                appointmentService.save(r.getAppointment());
                save(r);
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getClientCancelledReservations(String email) {
        List<Reservation> clientReservations = new ArrayList<Reservation>();
        for (Reservation reservation : findAll()) {
            if (reservation.getClient().getEmail().equals(email) && reservation.getCanceled().equals(true))
                clientReservations.add(reservation);
        }
        return clientReservations;
    }

    public List<Reservation> getClientReservationsForServiceProfile(String email, Integer serviceId) {
        List<Reservation> reservations = new ArrayList<Reservation>();
        for (Appointment ap : appointmentService.getAppointmentsByServiceId(serviceId)) {
            for (Reservation r : findAll()) {
                if (r.getAppointment().getAppointmentId().equals(ap.getAppointmentId())
                        && r.getClient().getEmail().equals(email)) {
                    reservations.add(r);
                    break;
                }
            }
        }

        return reservations;
    }

    public boolean overlapsWithDateRange(List<Reservation> reservations, Date start, Date end) {
        for (Reservation r : reservations) {
            if (r.getCanceled().equals(false))
                continue;
            if (start.equals(r.getAppointment().getStartDate()) || end.equals(r.getAppointment().getEndDate())
                    || (start.after(r.getAppointment().getStartDate()) && start.before(r.getAppointment().getEndDate()))
                    || (end.after(r.getAppointment().getStartDate()) && end.before(r.getAppointment().getEndDate()))
                    || (start.before(r.getAppointment().getStartDate()) && end.after(r.getAppointment().getEndDate())))
                return true;
        }

        return false;
    }
}
