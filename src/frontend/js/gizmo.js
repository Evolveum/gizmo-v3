import '../../../node_modules/popper.js/dist/umd/popper';
import '../../../node_modules/admin-lte/plugins/bootstrap/js/bootstrap';
import '../../../node_modules/admin-lte/dist/js/adminlte';

import '../../../node_modules/bootstrap-select';
import '../../../node_modules/bootstrap-multiselect';

import '../../../node_modules/moment/dist/moment'

import '../../../node_modules/daterangepicker/daterangepicker';

import '../../../node_modules/select2/dist/js/select2';

import '../../../node_modules/sparklines';

import '../../../node_modules/admin-lte/plugins/bootstrap-switch/js/bootstrap-switch';

import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';

import MidPointFullCalendar from "./fullCalendar";

window.MidPointFullCalendar = new MidPointFullCalendar();
window.interactionPlugin = interactionPlugin;
window.dayGridPlugin = dayGridPlugin;
window.timeGridPlugin = timeGridPlugin;
window.listPlugin = listPlugin;



