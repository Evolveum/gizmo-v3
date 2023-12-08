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

function initChart(elementId, chartData, labels) {

    var ctx = document.getElementById(elementId);
    var myDoughnutChart = new Chart(ctx, {
        type: 'doughnut',
        data : {
            datasets: [{
                data: chartData
            }],

            // These labels appear in the legend and in the tooltips when hovering different arcs
            labels: labels
        }
    });
}