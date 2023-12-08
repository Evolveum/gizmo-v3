/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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



