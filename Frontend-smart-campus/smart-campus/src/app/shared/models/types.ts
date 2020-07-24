export type PropertyType = 'CONFIG' | 'REPORTED' | 'INFORMATIVE';
export type ThingType = 'GATEWAY' | 'SENSOR' | 'ACTUATOR';

// Filter types.
export type ApplicationsFilter = 'ID' | 'NAME' | 'DESCRIPTION';
export type GatewaysFilter = 'ID' | 'NAME' | 'DESCRIPTION' | 'IP' | 'IS_ALIVE' | 'APPLICATION';
export type DevicesFilter = 'ID' | 'TYPE' | 'NAME' | 'DESCRIPTION' | 'GATEWAY';
export type ProcessesFilter = 'ID' | 'NAME' | 'DESCRIPTION' | 'IS_ALIVE' | 'GATEWAY';
export type PropertiesFilter = 'TYPE' | 'NAME' | 'VALUE';
export type UsersFilter = 'ID' | 'NAME' | 'USERNAME';
export type NotificationsFilter = 'GATEWAY' | 'PROCESS' | 'IS_ALIVE' | 'READ' | 'TIMESTAMP' | 'MESSAGE';
export type DataFilter = 'APPLICATION' | 'GATEWAY' | 'PROCESS' | 'TOPIC';
